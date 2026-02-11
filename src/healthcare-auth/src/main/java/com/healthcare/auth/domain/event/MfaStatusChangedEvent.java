package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record MfaStatusChangedEvent(
    UUID userId,
    String username,
    boolean enabled,
    Instant changedAt
) {
    public static MfaStatusChangedEvent enabled(UUID userId, String username) {
        return new MfaStatusChangedEvent(userId, username, true, Instant.now());
    }

    public static MfaStatusChangedEvent disabled(UUID userId, String username) {
        return new MfaStatusChangedEvent(userId, username, false, Instant.now());
    }
}
