package com.healthcare.auth.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserLoggedOutEvent(
    UUID userId,
    String username,
    boolean allDevices,
    Instant loggedOutAt
) {
    public static UserLoggedOutEvent singleDevice(UUID userId, String username) {
        return new UserLoggedOutEvent(userId, username, false, Instant.now());
    }

    public static UserLoggedOutEvent allDevices(UUID userId, String username) {
        return new UserLoggedOutEvent(userId, username, true, Instant.now());
    }
}
