package com.healthcare.notification.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record NotificationReadEvent(
    UUID eventId,
    Instant occurredAt,
    UUID notificationId,
    UUID userId
) implements DomainEvent {

    public NotificationReadEvent(UUID notificationId, UUID userId) {
        this(UUID.randomUUID(), Instant.now(), notificationId, userId);
    }

    @Override
    public UUID aggregateId() {
        return notificationId;
    }
}
