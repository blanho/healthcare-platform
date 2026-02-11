package com.healthcare.audit.service.impl;

import com.healthcare.audit.api.dto.ComplianceReportResponse;
import com.healthcare.audit.domain.*;
import com.healthcare.audit.repository.AuditEventRepository;
import com.healthcare.audit.service.ComplianceReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ComplianceReportServiceImpl implements ComplianceReportService {

    private static final Logger log = LoggerFactory.getLogger(ComplianceReportServiceImpl.class);

    private static final List<ResourceCategory> PHI_CATEGORIES = Arrays.stream(ResourceCategory.values())
        .filter(ResourceCategory::containsPhi)
        .toList();

    private static final List<AuditAction> SECURITY_ACTIONS = List.of(
        AuditAction.LOGIN, AuditAction.LOGOUT, AuditAction.LOGIN_FAILED,
        AuditAction.ACCESS_DENIED
    );

    private static final List<AuditOutcome> FAILED_OUTCOMES = List.of(
        AuditOutcome.FAILURE, AuditOutcome.DENIED, AuditOutcome.ERROR
    );

    private final AuditEventRepository auditEventRepository;

    public ComplianceReportServiceImpl(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Override
    public ComplianceReportResponse generateHipaaReport(Instant startTime, Instant endTime) {
        log.info("Generating HIPAA compliance report from {} to {}", startTime, endTime);

        Map<String, Long> eventsByCategory = getEventsByCategory(startTime, endTime);
        Map<String, Long> eventsByAction = getEventsByAction(startTime, endTime);
        Map<String, Long> eventsByOutcome = getEventsByOutcome(startTime, endTime);
        Map<String, Long> eventsBySeverity = getEventsBySeverity(startTime, endTime);

        long totalEvents = eventsByCategory.values().stream().mapToLong(Long::longValue).sum();
        long phiAccessEvents = auditEventRepository.countPhiAccessInRange(PHI_CATEGORIES, startTime, endTime);
        long securityEvents = countSecurityEvents(eventsByAction);
        long failedEvents = countFailedEvents(eventsByOutcome);
        long criticalEvents = eventsBySeverity.getOrDefault("CRITICAL", 0L);

        List<UUID> phiAccessors = auditEventRepository.findUsersWhoAccessedPhi(PHI_CATEGORIES, startTime, endTime);
        long uniquePhiAccessors = phiAccessors.size();

        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        List<Object[]> highActivityUsers = auditEventRepository.findUsersWithHighActivity(oneHourAgo, 100);
        long anomalousActivities = highActivityUsers.size();

        Map<String, Long> anomalyDetails = new HashMap<>();
        anomalyDetails.put("highActivityUsers", anomalousActivities);
        anomalyDetails.put("recentFailedLogins",
            eventsByAction.getOrDefault("LOGIN_FAILED", 0L));

        return ComplianceReportResponse.builder()
            .reportType("HIPAA_COMPLIANCE")
            .startTime(startTime)
            .endTime(endTime)
            .generatedAt(Instant.now())
            .totalEvents(totalEvents)
            .phiAccessEvents(phiAccessEvents)
            .securityEvents(securityEvents)
            .failedEvents(failedEvents)
            .criticalEvents(criticalEvents)
            .uniquePhiAccessors(uniquePhiAccessors)
            .afterHoursAccess(0L)
            .uniqueUsers(phiAccessors.size())
            .topPhiAccessors(Map.of())
            .eventsByCategory(eventsByCategory)
            .eventsByAction(eventsByAction)
            .eventsByOutcome(eventsByOutcome)
            .eventsBySeverity(eventsBySeverity)
            .anomalousActivities(anomalousActivities)
            .anomalyDetails(anomalyDetails)
            .build();
    }

    @Override
    public ComplianceReportResponse generateSecurityReport(Instant startTime, Instant endTime) {
        log.info("Generating security audit report from {} to {}", startTime, endTime);

        Map<String, Long> eventsByAction = getEventsByAction(startTime, endTime);
        Map<String, Long> eventsByOutcome = getEventsByOutcome(startTime, endTime);
        Map<String, Long> eventsBySeverity = getEventsBySeverity(startTime, endTime);

        long securityEvents = countSecurityEvents(eventsByAction);
        long failedEvents = countFailedEvents(eventsByOutcome);

        return ComplianceReportResponse.builder()
            .reportType("SECURITY_AUDIT")
            .startTime(startTime)
            .endTime(endTime)
            .generatedAt(Instant.now())
            .totalEvents(securityEvents)
            .securityEvents(securityEvents)
            .failedEvents(failedEvents)
            .criticalEvents(eventsBySeverity.getOrDefault("CRITICAL", 0L))
            .eventsByAction(eventsByAction)
            .eventsByOutcome(eventsByOutcome)
            .eventsBySeverity(eventsBySeverity)
            .build();
    }

    @Override
    public ComplianceReportResponse generatePhiAccessReport(Instant startTime, Instant endTime) {
        log.info("Generating PHI access report from {} to {}", startTime, endTime);

        long phiAccessEvents = auditEventRepository.countPhiAccessInRange(PHI_CATEGORIES, startTime, endTime);
        List<UUID> phiAccessors = auditEventRepository.findUsersWhoAccessedPhi(PHI_CATEGORIES, startTime, endTime);

        Map<String, Long> eventsByCategory = getEventsByCategory(startTime, endTime);
        Map<String, Long> phiCategoryCounts = eventsByCategory.entrySet().stream()
            .filter(e -> PHI_CATEGORIES.stream()
                .anyMatch(cat -> cat.name().equals(e.getKey())))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ComplianceReportResponse.builder()
            .reportType("PHI_ACCESS")
            .startTime(startTime)
            .endTime(endTime)
            .generatedAt(Instant.now())
            .totalEvents(phiAccessEvents)
            .phiAccessEvents(phiAccessEvents)
            .uniquePhiAccessors(phiAccessors.size())
            .uniqueUsers(phiAccessors.size())
            .eventsByCategory(phiCategoryCounts)
            .build();
    }

    @Override
    public ComplianceReportResponse generateDailySummary(Instant date) {
        LocalDate localDate = date.atZone(ZoneOffset.UTC).toLocalDate();
        Instant startTime = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endTime = localDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return generateHipaaReport(startTime, endTime);
    }

    @Override
    public ComplianceReportResponse generateWeeklySummary(Instant weekStart) {
        LocalDate startDate = weekStart.atZone(ZoneOffset.UTC).toLocalDate()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Instant startTime = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endTime = startDate.plusWeeks(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return generateHipaaReport(startTime, endTime);
    }

    @Override
    public ComplianceReportResponse generateMonthlySummary(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        Instant startTime = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endTime = startDate.plusMonths(1).atStartOfDay(ZoneOffset.UTC).toInstant();

        return generateHipaaReport(startTime, endTime);
    }

    private Map<String, Long> getEventsByCategory(Instant startTime, Instant endTime) {
        return auditEventRepository.countByResourceCategory(startTime, endTime).stream()
            .collect(Collectors.toMap(
                row -> ((ResourceCategory) row[0]).name(),
                row -> (Long) row[1]
            ));
    }

    private Map<String, Long> getEventsByAction(Instant startTime, Instant endTime) {
        return auditEventRepository.countByAction(startTime, endTime).stream()
            .collect(Collectors.toMap(
                row -> ((AuditAction) row[0]).name(),
                row -> (Long) row[1]
            ));
    }

    private Map<String, Long> getEventsByOutcome(Instant startTime, Instant endTime) {
        return auditEventRepository.countByOutcome(startTime, endTime).stream()
            .collect(Collectors.toMap(
                row -> ((AuditOutcome) row[0]).name(),
                row -> (Long) row[1]
            ));
    }

    private Map<String, Long> getEventsBySeverity(Instant startTime, Instant endTime) {
        return auditEventRepository.countBySeverity(startTime, endTime).stream()
            .collect(Collectors.toMap(
                row -> ((AuditSeverity) row[0]).name(),
                row -> (Long) row[1]
            ));
    }

    private long countSecurityEvents(Map<String, Long> eventsByAction) {
        return SECURITY_ACTIONS.stream()
            .mapToLong(action -> eventsByAction.getOrDefault(action.name(), 0L))
            .sum();
    }

    private long countFailedEvents(Map<String, Long> eventsByOutcome) {
        return FAILED_OUTCOMES.stream()
            .mapToLong(outcome -> eventsByOutcome.getOrDefault(outcome.name(), 0L))
            .sum();
    }
}
