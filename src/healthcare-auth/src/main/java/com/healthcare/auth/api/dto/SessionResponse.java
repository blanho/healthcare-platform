package com.healthcare.auth.api.dto;

import com.healthcare.auth.domain.UserSession;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
    UUID id,
    String deviceName,
    String deviceType,
    String browser,
    String operatingSystem,
    String ipAddress,
    String location,
    Instant lastActivityAt,
    boolean current
) {
    public static SessionResponse from(UserSession session, boolean isCurrent) {
        String location = buildLocation(session.getCountry(), session.getCity());

        return new SessionResponse(
            session.getId(),
            session.getDeviceName(),
            session.getDeviceType(),
            session.getBrowser(),
            session.getOperatingSystem(),
            session.getIpAddress(),
            location,
            session.getLastActivityAt(),
            isCurrent
        );
    }

    private static String buildLocation(String country, String city) {
        if (country == null && city == null) return null;
        if (city == null) return country;
        if (country == null) return city;
        return city + ", " + country;
    }
}
