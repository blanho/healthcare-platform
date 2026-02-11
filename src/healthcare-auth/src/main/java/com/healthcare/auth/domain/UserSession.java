package com.healthcare.auth.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions", indexes = {
    @Index(name = "idx_session_user_id", columnList = "user_id"),
    @Index(name = "idx_session_token_hash", columnList = "refresh_token_hash"),
    @Index(name = "idx_session_expires_at", columnList = "expires_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSession extends BaseEntity {

    private static final int IDLE_TIMEOUT_MINUTES = 30;
    private static final int ABSOLUTE_TIMEOUT_HOURS = 24;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, unique = true)
    private String refreshTokenHash;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "is_current")
    private boolean current = false;

    @Column(name = "revoked")
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_reason", length = 100)
    private String revokedReason;

    public UserSession(
            User user,
            String refreshTokenHash,
            String ipAddress,
            String userAgent,
            Instant expiresAt
    ) {
        this.user = user;
        this.refreshTokenHash = refreshTokenHash;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
        this.lastActivityAt = Instant.now();
        parseUserAgent(userAgent);
    }

    public boolean isValid() {
        if (revoked) return false;

        Instant now = Instant.now();

        if (now.isAfter(expiresAt)) return false;

        Instant idleLimit = lastActivityAt.plusSeconds(IDLE_TIMEOUT_MINUTES * 60L);
        return !now.isAfter(idleLimit);
    }

    public void recordActivity() {
        this.lastActivityAt = Instant.now();
    }

    public void revoke(String reason) {
        this.revoked = true;
        this.revokedAt = Instant.now();
        this.revokedReason = reason;
    }

    public void markAsCurrent() {
        this.current = true;
    }

    public void enrichWithGeoLocation(String country, String city) {
        this.country = country;
        this.city = city;
    }

    public String getDisplayName() {
        StringBuilder sb = new StringBuilder();
        if (browser != null) sb.append(browser);
        if (operatingSystem != null) {
            if (!sb.isEmpty()) sb.append(" on ");
            sb.append(operatingSystem);
        }
        if (sb.isEmpty()) {
            sb.append(deviceType != null ? deviceType : "Unknown Device");
        }
        return sb.toString();
    }

    private void parseUserAgent(String userAgent) {
        if (userAgent == null) {
            this.deviceType = "UNKNOWN";
            return;
        }

        String ua = userAgent.toLowerCase();

        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            if (ua.contains("android")) {
                this.deviceType = "MOBILE_ANDROID";
            } else if (ua.contains("iphone") || ua.contains("ipad")) {
                this.deviceType = "MOBILE_IOS";
            } else {
                this.deviceType = "MOBILE";
            }
        } else {
            this.deviceType = "WEB";
        }

        if (ua.contains("chrome") && !ua.contains("edge")) {
            this.browser = "Chrome";
        } else if (ua.contains("firefox")) {
            this.browser = "Firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            this.browser = "Safari";
        } else if (ua.contains("edge")) {
            this.browser = "Edge";
        }

        if (ua.contains("windows")) {
            this.operatingSystem = "Windows";
        } else if (ua.contains("mac os")) {
            this.operatingSystem = "macOS";
        } else if (ua.contains("linux")) {
            this.operatingSystem = "Linux";
        } else if (ua.contains("android")) {
            this.operatingSystem = "Android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            this.operatingSystem = "iOS";
        }

        this.deviceName = getDisplayName();
    }
}
