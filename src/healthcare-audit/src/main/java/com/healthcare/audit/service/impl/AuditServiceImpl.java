package com.healthcare.audit.service.impl;

import com.healthcare.audit.api.dto.*;
import com.healthcare.audit.domain.*;
import com.healthcare.audit.exception.AuditEventNotFoundException;
import com.healthcare.audit.exception.AuditLoggingException;
import com.healthcare.audit.repository.AuditEventRepository;
import com.healthcare.audit.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private static final List<ResourceCategory> PHI_CATEGORIES = Arrays.stream(ResourceCategory.values())
        .filter(ResourceCategory::containsPhi)
        .toList();

    private static final List<AuditAction> SECURITY_ACTIONS = List.of(
        AuditAction.LOGIN, AuditAction.LOGOUT, AuditAction.LOGIN_FAILED,
        AuditAction.ACCESS_DENIED, AuditAction.PASSWORD_CHANGE, AuditAction.PASSWORD_RESET,
        AuditAction.PERMISSION_GRANTED, AuditAction.PERMISSION_REVOKED,
        AuditAction.ROLE_ASSIGNED, AuditAction.ROLE_REVOKED
    );

    private static final List<AuditOutcome> FAILED_OUTCOMES = List.of(
        AuditOutcome.FAILURE, AuditOutcome.DENIED, AuditOutcome.ERROR
    );

    private static final long PHI_ACCESS_THRESHOLD_PER_HOUR = 100;
    private static final long EVENT_THRESHOLD_PER_HOUR = 500;
    private static final long FAILED_LOGIN_THRESHOLD = 5;

    private final AuditEventRepository auditEventRepository;

    public AuditServiceImpl(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logEvent(AuditEvent.Builder eventBuilder) {
        try {
            AuditEvent event = eventBuilder.build();
            AuditEvent saved = auditEventRepository.save(event);
            log.debug("Audit event logged: {} {} on {}/{}",
                saved.getAction(), saved.getOutcome(),
                saved.getResourceCategory(), saved.getResourceId());
            return saved;
        } catch (Exception e) {
            log.error("Failed to log audit event", e);
            throw new AuditLoggingException("Failed to log audit event", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logPhiAccess(UUID userId, String username, String userRole,
                                   ResourceCategory category, UUID resourceId,
                                   UUID patientId, AuditAction action, AuditOutcome outcome) {
        return logEvent(AuditEvent.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .resourceCategory(category)
            .resourceId(resourceId)
            .patientId(patientId)
            .action(action)
            .outcome(outcome)
            .description("PHI access: " + action.getDescription()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logAuthentication(UUID userId, String username, AuditAction action,
                                        AuditOutcome outcome, String clientIpHash) {
        return logEvent(AuditEvent.builder()
            .userId(userId)
            .username(username)
            .action(action)
            .outcome(outcome)
            .resourceCategory(ResourceCategory.USER)
            .resourceId(userId)
            .clientIpHash(clientIpHash)
            .description("Authentication: " + action.getDescription()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logModification(UUID userId, String username, String userRole,
                                      ResourceCategory category, UUID resourceId,
                                      UUID patientId, String changedFields) {
        return logEvent(AuditEvent.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .resourceCategory(category)
            .resourceId(resourceId)
            .patientId(patientId)
            .action(AuditAction.UPDATE)
            .outcome(AuditOutcome.SUCCESS)
            .changedFields(changedFields)
            .description("Data modification on " + category));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logExport(UUID userId, String username, String userRole,
                               ResourceCategory category, UUID resourceId,
                               UUID patientId, String exportDetails) {
        return logEvent(AuditEvent.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .resourceCategory(category)
            .resourceId(resourceId)
            .patientId(patientId)
            .action(AuditAction.EXPORT)
            .outcome(AuditOutcome.SUCCESS)
            .metadata(exportDetails)
            .severity(AuditSeverity.HIGH)
            .description("Data export from " + category));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditEvent logAccessDenied(UUID userId, String username, String userRole,
                                      ResourceCategory category, UUID resourceId,
                                      String reason) {
        return logEvent(AuditEvent.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .resourceCategory(category)
            .resourceId(resourceId)
            .action(AuditAction.ACCESS_DENIED)
            .outcome(AuditOutcome.DENIED)
            .severity(AuditSeverity.HIGH)
            .errorMessage(reason)
            .description("Access denied: " + reason));
    }

    @Override
    public Optional<AuditEventResponse> getEvent(UUID eventId) {
        return auditEventRepository.findById(eventId)
            .map(AuditEventResponse::from);
    }

    @Override
    public Page<AuditEventSummary> searchEvents(AuditSearchCriteria criteria, Pageable pageable) {
        Instant startTime = criteria.startTime() != null ?
            criteria.startTime() : Instant.now().minus(30, ChronoUnit.DAYS);
        Instant endTime = criteria.endTime() != null ?
            criteria.endTime() : Instant.now();

        return auditEventRepository.searchAuditEvents(
            criteria.userId(),
            criteria.patientId(),
            criteria.resourceCategory(),
            criteria.action(),
            criteria.outcome(),
            criteria.severity(),
            startTime,
            endTime,
            pageable
        ).map(AuditEventSummary::from);
    }

    @Override
    public Page<AuditEventSummary> getUserAuditTrail(UUID userId, Instant startTime,
                                                      Instant endTime, Pageable pageable) {
        Instant start = startTime != null ? startTime : Instant.now().minus(30, ChronoUnit.DAYS);
        Instant end = endTime != null ? endTime : Instant.now();

        return auditEventRepository.findByUserIdAndTimeRange(userId, start, end, pageable)
            .map(AuditEventSummary::from);
    }

    @Override
    public Page<AuditEventSummary> getPatientAuditTrail(UUID patientId, Instant startTime,
                                                         Instant endTime, Pageable pageable) {
        Instant start = startTime != null ? startTime : Instant.now().minus(30, ChronoUnit.DAYS);
        Instant end = endTime != null ? endTime : Instant.now();

        return auditEventRepository.findByPatientIdAndTimeRange(patientId, start, end, pageable)
            .map(AuditEventSummary::from);
    }

    @Override
    public Page<AuditEventSummary> getResourceAuditTrail(ResourceCategory category,
                                                          UUID resourceId, Pageable pageable) {
        return auditEventRepository
            .findByResourceCategoryAndResourceIdOrderByEventTimestampDesc(category, resourceId, pageable)
            .map(AuditEventSummary::from);
    }

    @Override
    public List<AuditEventResponse> getByCorrelationId(String correlationId) {
        return auditEventRepository.findByCorrelationIdOrderByEventTimestampAsc(correlationId)
            .stream()
            .map(AuditEventResponse::from)
            .toList();
    }

    @Override
    public Page<AuditEventSummary> getSecurityEvents(Instant since, Pageable pageable) {
        Instant effectiveSince = since != null ? since : Instant.now().minus(24, ChronoUnit.HOURS);
        return auditEventRepository.findSecurityEvents(SECURITY_ACTIONS, effectiveSince, pageable)
            .map(AuditEventSummary::from);
    }

    @Override
    public UserActivitySummary getUserActivitySummary(UUID userId, Instant startTime, Instant endTime) {
        Instant start = startTime != null ? startTime : Instant.now().minus(30, ChronoUnit.DAYS);
        Instant end = endTime != null ? endTime : Instant.now();

        Page<AuditEvent> userEvents = auditEventRepository.findByUserIdAndTimeRange(
            userId, start, end, PageRequest.of(0, 100));

        List<AuditEvent> events = userEvents.getContent();

        long phiAccessCount = events.stream()
            .filter(e -> e.isPhiAccess())
            .count();

        long modificationCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.UPDATE ||
                        e.getAction() == AuditAction.CREATE ||
                        e.getAction() == AuditAction.DELETE)
            .count();

        long exportCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.EXPORT ||
                        e.getAction() == AuditAction.DOWNLOAD ||
                        e.getAction() == AuditAction.PRINT)
            .count();

        long loginCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.LOGIN)
            .count();

        long failedLoginCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.LOGIN_FAILED)
            .count();

        long deniedCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.ACCESS_DENIED)
            .count();

        long uniquePatients = events.stream()
            .filter(e -> e.getPatientId() != null)
            .map(AuditEvent::getPatientId)
            .distinct()
            .count();

        long uniqueResources = events.stream()
            .filter(e -> e.getResourceId() != null)
            .map(AuditEvent::getResourceId)
            .distinct()
            .count();

        String username = events.isEmpty() ? null : events.get(0).getUsername();
        String userRole = events.isEmpty() ? null : events.get(0).getUserRole();

        List<AuditEventSummary> recentEvents = events.stream()
            .limit(10)
            .map(AuditEventSummary::from)
            .toList();

        boolean anomalous = hasAnomalousActivity(userId, Instant.now().minus(1, ChronoUnit.HOURS));

        return UserActivitySummary.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .periodStart(start)
            .periodEnd(end)
            .totalEvents(userEvents.getTotalElements())
            .phiAccessCount(phiAccessCount)
            .modificationCount(modificationCount)
            .exportCount(exportCount)
            .loginCount(loginCount)
            .failedLoginCount(failedLoginCount)
            .uniquePatientsAccessed(uniquePatients)
            .uniqueRecordsAccessed(uniqueResources)
            .afterHoursAccessCount(0)
            .deniedAccessCount(deniedCount)
            .hasAnomalousActivity(anomalous)
            .recentEvents(recentEvents)
            .build();
    }

    @Override
    public PatientAccessHistory getPatientAccessHistory(UUID patientId, Instant startTime, Instant endTime) {
        Instant start = startTime != null ? startTime : Instant.now().minus(365, ChronoUnit.DAYS);
        Instant end = endTime != null ? endTime : Instant.now();

        Page<AuditEvent> patientEvents = auditEventRepository.findByPatientIdAndTimeRange(
            patientId, start, end, PageRequest.of(0, 1000));

        List<AuditEvent> events = patientEvents.getContent();

        long viewCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.VIEW || e.getAction() == AuditAction.READ)
            .count();

        long modificationCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.UPDATE ||
                        e.getAction() == AuditAction.CREATE ||
                        e.getAction() == AuditAction.DELETE)
            .count();

        long exportCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.EXPORT ||
                        e.getAction() == AuditAction.DOWNLOAD)
            .count();

        long printCount = events.stream()
            .filter(e -> e.getAction() == AuditAction.PRINT)
            .count();

        Set<UUID> uniqueAccessors = events.stream()
            .map(AuditEvent::getUserId)
            .collect(Collectors.toSet());

        List<String> accessorRoles = events.stream()
            .map(AuditEvent::getUserRole)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        List<AuditEventSummary> accessEvents = events.stream()
            .limit(100)
            .map(AuditEventSummary::from)
            .toList();

        return PatientAccessHistory.builder()
            .patientId(patientId)
            .periodStart(start)
            .periodEnd(end)
            .totalAccessCount(patientEvents.getTotalElements())
            .viewCount(viewCount)
            .modificationCount(modificationCount)
            .exportCount(exportCount)
            .printCount(printCount)
            .uniqueAccessorCount(uniqueAccessors.size())
            .accessorRoles(accessorRoles)
            .accessEvents(accessEvents)
            .build();
    }

    @Override
    public boolean hasAnomalousActivity(UUID userId, Instant since) {
        long eventCount = auditEventRepository.countUserEventsSince(userId, since);
        long phiAccessCount = auditEventRepository.countUserPhiAccessSince(userId, PHI_CATEGORIES, since);
        long failedLogins = auditEventRepository.countUserActionSince(userId, AuditAction.LOGIN_FAILED, since);

        return eventCount > EVENT_THRESHOLD_PER_HOUR ||
               phiAccessCount > PHI_ACCESS_THRESHOLD_PER_HOUR ||
               failedLogins > FAILED_LOGIN_THRESHOLD;
    }

    @Override
    public List<UUID> getUsersWithHighActivity(Instant since, long threshold) {
        return auditEventRepository.findUsersWithHighActivity(since, threshold)
            .stream()
            .map(row -> (UUID) row[0])
            .toList();
    }

    @Override
    public long countUserPhiAccess(UUID userId, Instant since) {
        return auditEventRepository.countUserPhiAccessSince(userId, PHI_CATEGORIES, since);
    }

    @Override
    public long countFailedLogins(UUID userId, Instant since) {
        return auditEventRepository.countUserActionSince(userId, AuditAction.LOGIN_FAILED, since);
    }
}
