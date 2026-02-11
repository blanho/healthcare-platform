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

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_reset_token_hash", columnList = "token_hash"),
    @Index(name = "idx_reset_token_user_id", columnList = "user_id"),
    @Index(name = "idx_reset_token_expires_at", columnList = "expires_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordResetToken extends BaseEntity {

    private static final int TOKEN_VALIDITY_HOURS = 1;
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used")
    private boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    private PasswordResetToken(User user, String tokenHash, Instant expiresAt, String ipAddress, String userAgent) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.ipAddress = ipAddress;
        this.userAgent = truncateUserAgent(userAgent);
    }

    public static TokenWithHash create(User user, String ipAddress, String userAgent) {
        String plainToken = generateSecureToken();
        String tokenHash = hashToken(plainToken);
        Instant expiresAt = Instant.now().plusSeconds(TOKEN_VALIDITY_HOURS * 3600L);

        PasswordResetToken entity = new PasswordResetToken(user, tokenHash, expiresAt, ipAddress, userAgent);

        return new TokenWithHash(plainToken, entity);
    }

    public boolean isValid() {
        return !used && Instant.now().isBefore(expiresAt);
    }

    public void markAsUsed() {
        this.used = true;
        this.usedAt = Instant.now();
    }

    public record TokenWithHash(String plainToken, PasswordResetToken entity) {}

    private static String generateSecureToken() {
        byte[] bytes = new byte[TOKEN_LENGTH];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String hash(String plainToken) {
        return hashToken(plainToken);
    }

    private static String truncateUserAgent(String userAgent) {
        if (userAgent == null) return null;
        return userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent;
    }
}
