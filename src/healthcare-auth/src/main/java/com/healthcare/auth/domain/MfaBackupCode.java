package com.healthcare.auth.domain;

import com.healthcare.common.crypto.EncryptedStringConverter;
import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

@Entity
@Table(name = "mfa_backup_codes", indexes = {
    @Index(name = "idx_backup_code_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MfaBackupCode extends BaseEntity {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 8;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code_hash", nullable = false)
    @Convert(converter = EncryptedStringConverter.class)
    private String codeHash;

    @Column(name = "used")
    private boolean used = false;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "used_ip", length = 45)
    private String usedIp;

    private MfaBackupCode(User user, String codeHash) {
        this.user = user;
        this.codeHash = codeHash;
    }

    public static CodeWithHash generate(User user) {
        String plainCode = generateCode();
        String codeHash = hashCode(plainCode);

        MfaBackupCode entity = new MfaBackupCode(user, codeHash);

        return new CodeWithHash(plainCode, entity);
    }

    public boolean matches(String plainCode) {
        return !used && hashCode(plainCode).equals(this.codeHash);
    }

    public void markAsUsed(String ipAddress) {
        this.used = true;
        this.usedAt = Instant.now();
        this.usedIp = ipAddress;
    }

    public record CodeWithHash(String plainCode, MfaBackupCode entity) {}

    private static String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(SECURE_RANDOM.nextInt(10));
        }

        return code.substring(0, 4) + "-" + code.substring(4);
    }

    private static String hashCode(String code) {
        try {
            String normalized = code.replace("-", "");
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalized.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
