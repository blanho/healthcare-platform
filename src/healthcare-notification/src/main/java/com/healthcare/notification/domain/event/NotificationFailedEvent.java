package com.healthcare.notification.domain.event;

import com.healthcare.common.domain.DomainEvent;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationFailedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID notificationId,
    UUID userId,
    NotificationType type,
    NotificationCategory category,
    String failureReason,
    int retryCount
) implements DomainEvent {

    public NotificationFailedEvent(UUID notificationId, UUID userId,
                                   NotificationType type, NotificationCategory category,
                                   String failureReason, int retryCount) {
        this(UUID.randomUUID(), Instant.now(), notificationId, userId, type, category, failureReason, retryCount);
    }

    @Override
    public UUID aggregateId() {
        return notificationId;
    }
}
