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

@Entity
@Table(name = "password_history", indexes = {
    @Index(name = "idx_password_history_user_id", columnList = "user_id"),
    @Index(name = "idx_password_history_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "change_reason", length = 100)
    private String changeReason;

    @Column(name = "changed_by_admin")
    private boolean changedByAdmin = false;

    public PasswordHistory(User user, String passwordHash, String changeReason, boolean changedByAdmin) {
        this.user = user;
        this.passwordHash = passwordHash;
        this.changeReason = changeReason;
        this.changedByAdmin = changedByAdmin;
        this.createdAt = Instant.now();
    }

    public static PasswordHistory userChanged(User user, String passwordHash) {
        return new PasswordHistory(user, passwordHash, "User changed password", false);
    }

    public static PasswordHistory adminReset(User user, String passwordHash) {
        return new PasswordHistory(user, passwordHash, "Admin reset password", true);
    }

    public static PasswordHistory forgotPassword(User user, String passwordHash) {
        return new PasswordHistory(user, passwordHash, "Forgot password reset", false);
    }

    public static PasswordHistory initial(User user, String passwordHash) {
        return new PasswordHistory(user, passwordHash, "Initial password", false);
    }
}
