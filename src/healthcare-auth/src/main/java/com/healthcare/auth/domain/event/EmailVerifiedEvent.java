package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record EmailVerifiedEvent(
    UUID userId,
    String username,
    String email,
    Instant verifiedAt
) {
    public static EmailVerifiedEvent of(UUID userId, String username, String email) {
        return new EmailVerifiedEvent(userId, username, email, Instant.now());
    }
}
