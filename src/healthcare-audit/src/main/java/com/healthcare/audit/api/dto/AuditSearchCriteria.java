package com.healthcare.audit.api.dto;

import com.healthcare.audit.domain.*;
import java.time.Instant;
import java.util.UUID;

public record AuditSearchCriteria(
    UUID userId,
    UUID patientId,
    ResourceCategory resourceCategory,
    UUID resourceId,
    AuditAction action,
    AuditOutcome outcome,
    AuditSeverity severity,
    Instant startTime,
    Instant endTime,
    String correlationId,
    Boolean phiAccessOnly,
    Boolean securityEventsOnly
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private UUID patientId;
        private ResourceCategory resourceCategory;
        private UUID resourceId;
        private AuditAction action;
        private AuditOutcome outcome;
        private AuditSeverity severity;
        private Instant startTime;
        private Instant endTime;
        private String correlationId;
        private Boolean phiAccessOnly;
        private Boolean securityEventsOnly;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder resourceCategory(ResourceCategory resourceCategory) {
            this.resourceCategory = resourceCategory;
            return this;
        }

        public Builder resourceId(UUID resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder action(AuditAction action) {
            this.action = action;
            return this;
        }

        public Builder outcome(AuditOutcome outcome) {
            this.outcome = outcome;
            return this;
        }

        public Builder severity(AuditSeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder phiAccessOnly(Boolean phiAccessOnly) {
            this.phiAccessOnly = phiAccessOnly;
            return this;
        }

        public Builder securityEventsOnly(Boolean securityEventsOnly) {
            this.securityEventsOnly = securityEventsOnly;
            return this;
        }

        public AuditSearchCriteria build() {
            return new AuditSearchCriteria(
                userId, patientId, resourceCategory, resourceId,
                action, outcome, severity, startTime, endTime,
                correlationId, phiAccessOnly, securityEventsOnly
            );
        }
    }
}
