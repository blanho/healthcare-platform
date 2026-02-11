package com.healthcare.auth.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedInEvent(
    UUID eventId,
    Instant occurredAt,
    UUID userId,
    String username,
    String ipAddress
) implements DomainEvent {

    public UserLoggedInEvent(UUID userId, String username, String ipAddress) {
        this(UUID.randomUUID(), Instant.now(), userId, username, ipAddress);
    }

    @Override
    public UUID aggregateId() {
        return userId;
    }

    @Override
    public String eventType() {
        return "user.logged_in";
    }
}
