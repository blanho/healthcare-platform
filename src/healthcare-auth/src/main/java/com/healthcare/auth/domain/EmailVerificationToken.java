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
@Table(name = "email_verification_tokens", indexes = {
    @Index(name = "idx_email_token_hash", columnList = "token_hash"),
    @Index(name = "idx_email_token_user_id", columnList = "user_id"),
    @Index(name = "idx_email_token_expires_at", columnList = "expires_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerificationToken extends BaseEntity {

    private static final int TOKEN_VALIDITY_HOURS = 24;
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used")
    private boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;

    private EmailVerificationToken(User user, String tokenHash, String email, Instant expiresAt) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.email = email;
        this.expiresAt = expiresAt;
    }

    public static TokenWithHash create(User user, String email) {
        String plainToken = generateSecureToken();
        String tokenHash = hashToken(plainToken);
        Instant expiresAt = Instant.now().plusSeconds(TOKEN_VALIDITY_HOURS * 3600L);

        EmailVerificationToken entity = new EmailVerificationToken(user, tokenHash, email, expiresAt);

        return new TokenWithHash(plainToken, entity);
    }

    public boolean isValid() {
        return !used && Instant.now().isBefore(expiresAt);
    }

    public void markAsUsed() {
        this.used = true;
        this.usedAt = Instant.now();
    }

    public record TokenWithHash(String plainToken, EmailVerificationToken entity) {}

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
}
