package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RoleChangedEvent(
    UUID userId,
    String username,
    Set<String> previousRoles,
    Set<String> newRoles,
    String changedByUserId,
    Instant changedAt
) {
    public static RoleChangedEvent of(
            UUID userId,
            String username,
            Set<String> previousRoles,
            Set<String> newRoles,
            String changedByUserId
    ) {
        return new RoleChangedEvent(userId, username, previousRoles, newRoles, changedByUserId, Instant.now());
    }
}
