package com.healthcare.audit.service;

import com.healthcare.audit.api.dto.*;
import com.healthcare.audit.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditService {

    AuditEvent logEvent(AuditEvent.Builder eventBuilder);

    AuditEvent logPhiAccess(UUID userId, String username, String userRole,
                           ResourceCategory category, UUID resourceId,
                           UUID patientId, AuditAction action, AuditOutcome outcome);

    AuditEvent logAuthentication(UUID userId, String username, AuditAction action,
                                AuditOutcome outcome, String clientIpHash);

    AuditEvent logModification(UUID userId, String username, String userRole,
                              ResourceCategory category, UUID resourceId,
                              UUID patientId, String changedFields);

    AuditEvent logExport(UUID userId, String username, String userRole,
                        ResourceCategory category, UUID resourceId,
                        UUID patientId, String exportDetails);

    AuditEvent logAccessDenied(UUID userId, String username, String userRole,
                              ResourceCategory category, UUID resourceId,
                              String reason);

    Optional<AuditEventResponse> getEvent(UUID eventId);

    Page<AuditEventSummary> searchEvents(AuditSearchCriteria criteria, Pageable pageable);

    Page<AuditEventSummary> getUserAuditTrail(UUID userId, Instant startTime,
                                               Instant endTime, Pageable pageable);

    Page<AuditEventSummary> getPatientAuditTrail(UUID patientId, Instant startTime,
                                                  Instant endTime, Pageable pageable);

    Page<AuditEventSummary> getResourceAuditTrail(ResourceCategory category, UUID resourceId,
                                                   Pageable pageable);

    List<AuditEventResponse> getByCorrelationId(String correlationId);

    Page<AuditEventSummary> getSecurityEvents(Instant since, Pageable pageable);

    UserActivitySummary getUserActivitySummary(UUID userId, Instant startTime, Instant endTime);

    PatientAccessHistory getPatientAccessHistory(UUID patientId, Instant startTime, Instant endTime);

    boolean hasAnomalousActivity(UUID userId, Instant since);

    List<UUID> getUsersWithHighActivity(Instant since, long threshold);

    long countUserPhiAccess(UUID userId, Instant since);

    long countFailedLogins(UUID userId, Instant since);
}
