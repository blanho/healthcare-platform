package com.healthcare.notification.domain.event;

import com.healthcare.common.domain.DomainEvent;
import com.healthcare.notification.domain.NotificationCategory;
import com.healthcare.notification.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationSentEvent(
    UUID eventId,
    Instant occurredAt,
    UUID notificationId,
    UUID userId,
    NotificationType type,
    NotificationCategory category
) implements DomainEvent {

    public NotificationSentEvent(UUID notificationId, UUID userId,
                                 NotificationType type, NotificationCategory category) {
        this(UUID.randomUUID(), Instant.now(), notificationId, userId, type, category);
    }

    @Override
    public UUID aggregateId() {
        return notificationId;
    }
}
