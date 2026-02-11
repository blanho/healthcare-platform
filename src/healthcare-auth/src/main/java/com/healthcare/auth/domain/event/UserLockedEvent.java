package com.healthcare.auth.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record UserLockedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID userId,
    String username,
    int failedAttempts
) implements DomainEvent {

    public UserLockedEvent(UUID userId, String username, int failedAttempts) {
        this(UUID.randomUUID(), Instant.now(), userId, username, failedAttempts);
    }

    @Override
    public UUID aggregateId() {
        return userId;
    }

    @Override
    public String eventType() {
        return "user.locked";
    }
}
