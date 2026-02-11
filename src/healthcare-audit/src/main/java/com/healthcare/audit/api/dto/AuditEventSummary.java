package com.healthcare.audit.api.dto;

import com.healthcare.audit.domain.*;
import java.time.Instant;
import java.util.UUID;

public record AuditEventSummary(
    UUID id,
    Instant eventTimestamp,
    UUID userId,
    String username,
    AuditAction action,
    AuditOutcome outcome,
    AuditSeverity severity,
    ResourceCategory resourceCategory,
    UUID resourceId,
    UUID patientId,
    boolean phiAccess,
    boolean securityConcern
) {
    public static AuditEventSummary from(AuditEvent event) {
        return new AuditEventSummary(
            event.getId(),
            event.getEventTimestamp(),
            event.getUserId(),
            event.getUsername(),
            event.getAction(),
            event.getOutcome(),
            event.getSeverity(),
            event.getResourceCategory(),
            event.getResourceId(),
            event.getPatientId(),
            event.isPhiAccess(),
            event.isSecurityConcern()
        );
    }
}
