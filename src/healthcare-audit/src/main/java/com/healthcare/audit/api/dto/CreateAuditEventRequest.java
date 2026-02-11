package com.healthcare.audit.api.dto;

import com.healthcare.audit.domain.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public record CreateAuditEventRequest(
    @NotNull(message = "User ID is required")
    UUID userId,

    String username,

    String userRole,

    @NotNull(message = "Action is required")
    AuditAction action,

    @NotNull(message = "Outcome is required")
    AuditOutcome outcome,

    AuditSeverity severity,

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    @NotNull(message = "Resource category is required")
    ResourceCategory resourceCategory,

    UUID resourceId,

    String resourceType,

    UUID patientId,

    String accessedFields,

    String changedFields,

    String correlationId,

    String sessionId,

    String metadata
) {
    public AuditEvent toEntity(String clientIpHash, String userAgent) {
        return AuditEvent.builder()
            .userId(userId)
            .username(username)
            .userRole(userRole)
            .action(action)
            .outcome(outcome)
            .severity(severity)
            .description(description)
            .resourceCategory(resourceCategory)
            .resourceId(resourceId)
            .resourceType(resourceType)
            .patientId(patientId)
            .accessedFields(accessedFields)
            .changedFields(changedFields)
            .correlationId(correlationId)
            .sessionId(sessionId)
            .clientIpHash(clientIpHash)
            .userAgent(userAgent)
            .metadata(metadata)
            .build();
    }
}
