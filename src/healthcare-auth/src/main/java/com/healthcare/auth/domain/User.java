package com.healthcare.auth.domain;

import com.healthcare.auth.domain.event.UserLockedEvent;
import com.healthcare.auth.domain.event.UserLoggedInEvent;
import com.healthcare.common.crypto.EncryptedStringConverter;
import com.healthcare.common.domain.AggregateRoot;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AggregateRoot {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Setter
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    @Setter
    private String firstName;

    @Column(name = "last_name", length = 100)
    @Setter
    private String lastName;

    @Column(name = "phone_number", length = 20)
    @Setter
    private String phoneNumber;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "provider_id")
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "must_change_password")
    private boolean mustChangePassword = false;

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled = false;

    @Column(name = "mfa_secret", length = 255)
    @Convert(converter = EncryptedStringConverter.class)
    private String mfaSecret;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @BatchSize(size = 25)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshToken> refreshTokens = new HashSet<>();

    @Builder
    public User(
            String username,
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            String phoneNumber,
            UUID patientId,
            UUID providerId
    ) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.patientId = patientId;
        this.providerId = providerId;
        this.status = UserStatus.ACTIVE;
        this.passwordChangedAt = Instant.now();
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return username;
        }
        return ((firstName != null ? firstName : "") + " " +
                (lastName != null ? lastName : "")).trim();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean hasPermission(String permissionName) {
        return roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    public boolean hasPermission(String resource, String action) {
        return roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(p -> p.getResource().equals(resource) && p.getAction().equals(action));
    }

    public Set<String> getAllPermissions() {
        Set<String> permissions = new HashSet<>();
        for (Role role : roles) {
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getName());
            }
        }
        return permissions;
    }

    public Set<String> getRoleNames() {
        Set<String> roleNames = new HashSet<>();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    public void recordFailedLogin() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
            this.lockedUntil = Instant.now().plusSeconds(LOCKOUT_DURATION_MINUTES * 60L);
            registerEvent(new UserLockedEvent(getId(), username, failedLoginAttempts));
        }
    }

    public void recordSuccessfulLogin(String ipAddress) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.lastLoginAt = Instant.now();
        this.lastLoginIp = ipAddress;
        registerEvent(new UserLoggedInEvent(getId(), username, ipAddress));
    }

    public boolean isLocked() {
        return lockedUntil != null && Instant.now().isBefore(lockedUntil);
    }

    public boolean canLogin() {
        return status == UserStatus.ACTIVE && !isLocked();
    }

    public void unlock() {
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.emailVerifiedAt = Instant.now();
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.passwordChangedAt = Instant.now();
        this.mustChangePassword = false;

        this.refreshTokens.forEach(RefreshToken::revoke);
    }

    public void requirePasswordChange() {
        this.mustChangePassword = true;
    }

    public void enableMfa(String secret) {
        this.mfaEnabled = true;
        this.mfaSecret = secret;
    }

    public void disableMfa() {
        this.mfaEnabled = false;
        this.mfaSecret = null;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.refreshTokens.forEach(RefreshToken::revoke);
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.refreshTokens.forEach(RefreshToken::revoke);
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void linkToPatient(UUID patientId) {
        this.patientId = patientId;
    }

    public void linkToProvider(UUID providerId) {
        this.providerId = providerId;
    }
}
