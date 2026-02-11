package com.healthcare.auth.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_attempts", indexes = {
    @Index(name = "idx_login_attempt_username", columnList = "username"),
    @Index(name = "idx_login_attempt_ip", columnList = "ip_address"),
    @Index(name = "idx_login_attempt_time", columnList = "attempted_at"),
    @Index(name = "idx_login_attempt_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginAttempt extends BaseEntity {

    public enum Status {
        SUCCESS,
        FAILED_INVALID_CREDENTIALS,
        FAILED_ACCOUNT_LOCKED,
        FAILED_ACCOUNT_INACTIVE,
        FAILED_MFA_REQUIRED,
        FAILED_MFA_INVALID,
        FAILED_CAPTCHA_REQUIRED,
        FAILED_CAPTCHA_INVALID
    }

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "username", nullable = false, length = 255)
    private String username;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    public static LoginAttempt success(UUID userId, String username, String ipAddress, String userAgent) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.userId = userId;
        attempt.username = username;
        attempt.ipAddress = ipAddress;
        attempt.userAgent = truncateUserAgent(userAgent);
        attempt.status = Status.SUCCESS;
        attempt.attemptedAt = Instant.now();
        return attempt;
    }

    public static LoginAttempt failed(
            String username,
            String ipAddress,
            String userAgent,
            Status status,
            String reason
    ) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.username = username;
        attempt.ipAddress = ipAddress;
        attempt.userAgent = truncateUserAgent(userAgent);
        attempt.status = status;
        attempt.failureReason = reason;
        attempt.attemptedAt = Instant.now();
        return attempt;
    }

    public static LoginAttempt failedWithUser(
            UUID userId,
            String username,
            String ipAddress,
            String userAgent,
            Status status,
            String reason
    ) {
        LoginAttempt attempt = failed(username, ipAddress, userAgent, status, reason);
        attempt.userId = userId;
        return attempt;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public void enrichWithGeoLocation(String country, String city) {
        this.country = country;
        this.city = city;
    }

    private static String truncateUserAgent(String userAgent) {
        if (userAgent == null) return null;
        return userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent;
    }
}
