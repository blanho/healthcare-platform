package com.healthcare.auth.service;

import com.healthcare.auth.domain.LoginAttempt;

import java.util.UUID;

public interface LoginAttemptService {

    void recordSuccess(UUID userId, String username, String ipAddress, String userAgent);

    void recordFailure(
        String username,
        String ipAddress,
        String userAgent,
        LoginAttempt.Status status,
        String reason
    );

    void recordFailureWithUser(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent,
        LoginAttempt.Status status,
        String reason
    );

    boolean isIpBlocked(String ipAddress);

    boolean isUsernameBlocked(String username);

    boolean shouldRequireCaptcha(String ipAddress, String username);

    long getRecentFailedAttemptCount(String username);
}
