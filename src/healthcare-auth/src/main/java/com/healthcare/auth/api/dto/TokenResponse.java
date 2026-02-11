package com.healthcare.auth.api.dto;

import java.time.Instant;
import java.util.Set;

public record TokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    Instant expiresAt,
    Set<String> roles,
    Set<String> permissions
) {

    public static TokenResponse of(
            String accessToken,
            String refreshToken,
            long expiresInSeconds,
            Set<String> roles,
            Set<String> permissions
    ) {
        return new TokenResponse(
            accessToken,
            refreshToken,
            "Bearer",
            expiresInSeconds,
            Instant.now().plusSeconds(expiresInSeconds),
            roles,
            permissions
        );
    }
}
