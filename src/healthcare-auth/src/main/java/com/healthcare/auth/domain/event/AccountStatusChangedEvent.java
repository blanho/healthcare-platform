package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record AccountStatusChangedEvent(
    UUID userId,
    String username,
    String previousStatus,
    String newStatus,
    String reason,
    String changedByUserId,
    Instant changedAt
) {
    public static AccountStatusChangedEvent of(
            UUID userId,
            String username,
            String previousStatus,
            String newStatus,
            String reason,
            String changedByUserId
    ) {
        return new AccountStatusChangedEvent(
            userId, username, previousStatus, newStatus, reason, changedByUserId, Instant.now()
        );
    }
}
