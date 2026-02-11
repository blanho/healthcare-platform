package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PasswordChangedEvent(
    UUID userId,
    String username,
    String changeReason,
    boolean changedByAdmin,
    Instant changedAt
) {
    public static PasswordChangedEvent userChanged(UUID userId, String username) {
        return new PasswordChangedEvent(userId, username, "User changed password", false, Instant.now());
    }

    public static PasswordChangedEvent adminReset(UUID userId, String username) {
        return new PasswordChangedEvent(userId, username, "Admin reset password", true, Instant.now());
    }

    public static PasswordChangedEvent forgotPassword(UUID userId, String username) {
        return new PasswordChangedEvent(userId, username, "Forgot password reset", false, Instant.now());
    }
}
