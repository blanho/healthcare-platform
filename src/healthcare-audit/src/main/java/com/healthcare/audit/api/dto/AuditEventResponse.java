package com.healthcare.audit.api.dto;

import com.healthcare.audit.domain.*;
import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
    UUID id,
    Instant eventTimestamp,
    String correlationId,
    String sessionId,

    UUID userId,
    String username,
    String userRole,

    AuditAction action,
    AuditOutcome outcome,
    AuditSeverity severity,
    String description,

    ResourceCategory resourceCategory,
    UUID resourceId,
    String resourceType,
    UUID patientId,

    String accessedFields,
    String changedFields,

    String httpMethod,
    String requestUri,
    Integer responseStatus,
    Long responseTimeMs,

    String errorCode,
    String errorMessage,

    boolean phiAccess,
    boolean securityConcern
) {
    public static AuditEventResponse from(AuditEvent event) {
        return new AuditEventResponse(
            event.getId(),
            event.getEventTimestamp(),
            event.getCorrelationId(),
            event.getSessionId(),
            event.getUserId(),
            event.getUsername(),
            event.getUserRole(),
            event.getAction(),
            event.getOutcome(),
            event.getSeverity(),
            event.getDescription(),
            event.getResourceCategory(),
            event.getResourceId(),
            event.getResourceType(),
            event.getPatientId(),
            event.getAccessedFields(),
            event.getChangedFields(),
            event.getHttpMethod(),
            event.getRequestUri(),
            event.getResponseStatus(),
            event.getResponseTimeMs(),
            event.getErrorCode(),
            event.getErrorMessage(),
            event.isPhiAccess(),
            event.isSecurityConcern()
        );
    }
}
