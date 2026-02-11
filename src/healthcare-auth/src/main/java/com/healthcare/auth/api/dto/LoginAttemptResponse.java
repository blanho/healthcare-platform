package com.healthcare.auth.api.dto;

import com.healthcare.auth.domain.LoginAttempt;

import java.time.Instant;
import java.util.UUID;

public record LoginAttemptResponse(
    UUID id,
    String ipAddress,
    String userAgent,
    String status,
    Instant attemptedAt,
    String location,
    boolean successful
) {
    public static LoginAttemptResponse from(LoginAttempt attempt) {
        String location = buildLocation(attempt.getCountry(), attempt.getCity());

        return new LoginAttemptResponse(
            attempt.getId(),
            attempt.getIpAddress(),
            attempt.getUserAgent(),
            attempt.getStatus().name(),
            attempt.getAttemptedAt(),
            location,
            attempt.isSuccess()
        );
    }

    private static String buildLocation(String country, String city) {
        if (country == null && city == null) return null;
        if (city == null) return country;
        if (country == null) return city;
        return city + ", " + country;
    }
}
