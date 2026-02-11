package com.healthcare.audit.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {
    @Index(name = "idx_audit_event_timestamp", columnList = "event_timestamp"),
    @Index(name = "idx_audit_event_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_event_resource", columnList = "resource_category, resource_id"),
    @Index(name = "idx_audit_event_action", columnList = "action"),
    @Index(name = "idx_audit_event_patient_id", columnList = "patient_id"),
    @Index(name = "idx_audit_event_correlation_id", columnList = "correlation_id")
})
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "event_timestamp", nullable = false, updatable = false)
    private Instant eventTimestamp;

    @Column(name = "correlation_id", length = 64, updatable = false)
    private String correlationId;

    @Column(name = "session_id", length = 64, updatable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "username", length = 100, updatable = false)
    private String username;

    @Column(name = "user_role", length = 50, updatable = false)
    private String userRole;

    @Column(name = "client_ip_hash", length = 64, updatable = false)
    private String clientIpHash;

    @Column(name = "user_agent", length = 255, updatable = false)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30, updatable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", nullable = false, length = 20, updatable = false)
    private AuditOutcome outcome;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20, updatable = false)
    private AuditSeverity severity;

    @Column(name = "description", length = 500, updatable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_category", nullable = false, length = 30, updatable = false)
    private ResourceCategory resourceCategory;

    @Column(name = "resource_id", updatable = false)
    private UUID resourceId;

    @Column(name = "resource_type", length = 100, updatable = false)
    private String resourceType;

    @Column(name = "patient_id", updatable = false)
    private UUID patientId;

    @Column(name = "accessed_fields", columnDefinition = "TEXT", updatable = false)
    private String accessedFields;

    @Column(name = "changed_fields", columnDefinition = "TEXT", updatable = false)
    private String changedFields;

    @Column(name = "http_method", length = 10, updatable = false)
    private String httpMethod;

    @Column(name = "request_uri", length = 500, updatable = false)
    private String requestUri;

    @Column(name = "response_status", updatable = false)
    private Integer responseStatus;

    @Column(name = "response_time_ms", updatable = false)
    private Long responseTimeMs;

    @Column(name = "error_code", length = 50, updatable = false)
    private String errorCode;

    @Column(name = "error_message", length = 500, updatable = false)
    private String errorMessage;

    @Column(name = "metadata", columnDefinition = "TEXT", updatable = false)
    private String metadata;

    @Column(name = "checksum", length = 64, updatable = false)
    private String checksum;

    @Column(name = "schema_version", length = 10, updatable = false)
    private String schemaVersion = "1.0";

    protected AuditEvent() {

    }

    private AuditEvent(Builder builder) {
        this.eventTimestamp = builder.eventTimestamp != null ? builder.eventTimestamp : Instant.now();
        this.correlationId = builder.correlationId;
        this.sessionId = builder.sessionId;
        this.userId = builder.userId;
        this.username = builder.username;
        this.userRole = builder.userRole;
        this.clientIpHash = builder.clientIpHash;
        this.userAgent = truncate(builder.userAgent, 255);
        this.action = builder.action;
        this.outcome = builder.outcome;
        this.severity = builder.severity != null ? builder.severity :
            AuditSeverity.forActionAndOutcome(builder.action, builder.outcome);
        this.description = truncate(builder.description, 500);
        this.resourceCategory = builder.resourceCategory;
        this.resourceId = builder.resourceId;
        this.resourceType = builder.resourceType;
        this.patientId = builder.patientId;
        this.accessedFields = builder.accessedFields;
        this.changedFields = builder.changedFields;
        this.httpMethod = builder.httpMethod;
        this.requestUri = truncate(builder.requestUri, 500);
        this.responseStatus = builder.responseStatus;
        this.responseTimeMs = builder.responseTimeMs;
        this.errorCode = builder.errorCode;
        this.errorMessage = truncate(builder.errorMessage, 500);
        this.metadata = builder.metadata;
        this.schemaVersion = "1.0";
    }

    public UUID getId() { return id; }
    public Instant getEventTimestamp() { return eventTimestamp; }
    public String getCorrelationId() { return correlationId; }
    public String getSessionId() { return sessionId; }
    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserRole() { return userRole; }
    public String getClientIpHash() { return clientIpHash; }
    public String getUserAgent() { return userAgent; }
    public AuditAction getAction() { return action; }
    public AuditOutcome getOutcome() { return outcome; }
    public AuditSeverity getSeverity() { return severity; }
    public String getDescription() { return description; }
    public ResourceCategory getResourceCategory() { return resourceCategory; }
    public UUID getResourceId() { return resourceId; }
    public String getResourceType() { return resourceType; }
    public UUID getPatientId() { return patientId; }
    public String getAccessedFields() { return accessedFields; }
    public String getChangedFields() { return changedFields; }
    public String getHttpMethod() { return httpMethod; }
    public String getRequestUri() { return requestUri; }
    public Integer getResponseStatus() { return responseStatus; }
    public Long getResponseTimeMs() { return responseTimeMs; }
    public String getErrorCode() { return errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public String getMetadata() { return metadata; }
    public String getChecksum() { return checksum; }
    public String getSchemaVersion() { return schemaVersion; }

    public boolean isPhiAccess() {
        return resourceCategory != null && resourceCategory.containsPhi();
    }

    public boolean isSecurityConcern() {
        return outcome == AuditOutcome.DENIED ||
               outcome == AuditOutcome.ERROR ||
               action == AuditAction.ACCESS_DENIED ||
               action == AuditAction.LOGIN_FAILED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant eventTimestamp;
        private String correlationId;
        private String sessionId;
        private UUID userId;
        private String username;
        private String userRole;
        private String clientIpHash;
        private String userAgent;
        private AuditAction action;
        private AuditOutcome outcome;
        private AuditSeverity severity;
        private String description;
        private ResourceCategory resourceCategory;
        private UUID resourceId;
        private String resourceType;
        private UUID patientId;
        private String accessedFields;
        private String changedFields;
        private String httpMethod;
        private String requestUri;
        private Integer responseStatus;
        private Long responseTimeMs;
        private String errorCode;
        private String errorMessage;
        private String metadata;

        public Builder eventTimestamp(Instant eventTimestamp) {
            this.eventTimestamp = eventTimestamp;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder userRole(String userRole) {
            this.userRole = userRole;
            return this;
        }

        public Builder clientIpHash(String clientIpHash) {
            this.clientIpHash = clientIpHash;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
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

        public Builder description(String description) {
            this.description = description;
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

        public Builder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder accessedFields(String accessedFields) {
            this.accessedFields = accessedFields;
            return this;
        }

        public Builder changedFields(String changedFields) {
            this.changedFields = changedFields;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder responseStatus(Integer responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public Builder responseTimeMs(Long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
            return this;
        }

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public AuditEvent build() {
            validate();
            return new AuditEvent(this);
        }

        private void validate() {
            if (userId == null) {
                throw new IllegalStateException("userId is required");
            }
            if (action == null) {
                throw new IllegalStateException("action is required");
            }
            if (outcome == null) {
                throw new IllegalStateException("outcome is required");
            }
            if (resourceCategory == null) {
                throw new IllegalStateException("resourceCategory is required");
            }
        }
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    @Override
    public String toString() {
        return "AuditEvent{" +
            "id=" + id +
            ", timestamp=" + eventTimestamp +
            ", action=" + action +
            ", outcome=" + outcome +
            ", resourceCategory=" + resourceCategory +
            ", resourceId=" + resourceId +
            '}';
    }
}
