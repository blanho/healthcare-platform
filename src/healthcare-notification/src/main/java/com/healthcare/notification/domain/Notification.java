package com.healthcare.notification.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_patient", columnList = "patient_id"),
    @Index(name = "idx_notification_status", columnList = "status")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "patient_id")
    private UUID patientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private NotificationCategory category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "retry_count")
    private int retryCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Transient
    private NotificationRecipient recipient;

    protected Notification() {

    }

    private Notification(Builder builder) {
        this.userId = builder.userId;
        this.patientId = builder.patientId;
        this.type = builder.type;
        this.category = builder.category;
        this.title = builder.title;
        this.message = builder.message;
        this.status = builder.scheduledAt != null ? NotificationStatus.SCHEDULED : NotificationStatus.PENDING;
        this.scheduledAt = builder.scheduledAt;
        this.metadata = builder.metadata;
        this.retryCount = 0;
        this.createdAt = Instant.now();
        this.recipient = builder.recipient;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Notification createImmediate(
            UUID userId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String message) {
        return builder()
            .userId(userId)
            .type(type)
            .category(category)
            .title(title)
            .message(message)
            .build();
    }

    public static Notification createScheduled(
            UUID userId,
            NotificationType type,
            NotificationCategory category,
            String title,
            String message,
            Instant scheduledAt) {
        return builder()
            .userId(userId)
            .type(type)
            .category(category)
            .title(title)
            .message(message)
            .scheduledAt(scheduledAt)
            .build();
    }

    public void markSending() {
        if (this.status != NotificationStatus.PENDING && this.status != NotificationStatus.SCHEDULED) {
            throw new IllegalStateException("Can only send pending or scheduled notifications");
        }
        this.status = NotificationStatus.SENDING;
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    public void markDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        if (this.sentAt == null) {
            this.sentAt = this.deliveredAt;
        }
    }

    public void markRead() {
        if (this.status != NotificationStatus.DELIVERED && this.status != NotificationStatus.SENT) {
            return;
        }
        this.status = NotificationStatus.READ;
        this.readAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failedAt = Instant.now();
        this.failureReason = reason;
    }

    public void cancel() {
        if (!this.status.canCancel()) {
            throw new IllegalStateException("Cannot cancel notification in status: " + this.status);
        }
        this.status = NotificationStatus.CANCELLED;
    }

    public void retry() {
        if (!this.status.canRetry()) {
            throw new IllegalStateException("Cannot retry notification in status: " + this.status);
        }
        this.status = NotificationStatus.PENDING;
        this.retryCount++;
        this.failedAt = null;
        this.failureReason = null;
    }

    public boolean isReadyToSend() {
        if (this.status == NotificationStatus.PENDING) {
            return true;
        }
        if (this.status == NotificationStatus.SCHEDULED && this.scheduledAt != null) {
            return !Instant.now().isBefore(this.scheduledAt);
        }
        return false;
    }

    public boolean canRetry(int maxRetries) {
        return this.status == NotificationStatus.FAILED && this.retryCount < maxRetries;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getPatientId() { return patientId; }
    public NotificationType getType() { return type; }
    public NotificationCategory getCategory() { return category; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public Instant getScheduledAt() { return scheduledAt; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public Instant getReadAt() { return readAt; }
    public Instant getFailedAt() { return failedAt; }
    public String getFailureReason() { return failureReason; }
    public int getRetryCount() { return retryCount; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Instant getCreatedAt() { return createdAt; }
    public NotificationRecipient getRecipient() { return recipient; }

    public void setRecipient(NotificationRecipient recipient) {
        this.recipient = recipient;
    }

    public static class Builder {
        private UUID userId;
        private UUID patientId;
        private NotificationType type;
        private NotificationCategory category;
        private String title;
        private String message;
        private Instant scheduledAt;
        private Map<String, Object> metadata;
        private NotificationRecipient recipient;

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder category(NotificationCategory category) {
            this.category = category;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder scheduledAt(Instant scheduledAt) {
            this.scheduledAt = scheduledAt;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder recipient(NotificationRecipient recipient) {
            this.recipient = recipient;
            return this;
        }

        public Notification build() {
            if (type == null) {
                throw new IllegalStateException("Notification type is required");
            }
            if (category == null) {
                throw new IllegalStateException("Notification category is required");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalStateException("Notification title is required");
            }
            if (message == null || message.isBlank()) {
                throw new IllegalStateException("Notification message is required");
            }
            return new Notification(this);
        }
    }
}
