package com.healthcare.auth.service;

import com.healthcare.auth.domain.PasswordHistory;
import com.healthcare.auth.domain.PasswordResetToken;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.domain.event.PasswordChangedEvent;
import com.healthcare.auth.exception.AuthenticationException;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.repository.PasswordHistoryRepository;
import com.healthcare.auth.repository.PasswordResetTokenRepository;
import com.healthcare.auth.repository.RefreshTokenRepository;
import com.healthcare.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
class PasswordServiceImpl implements PasswordService {

    private static final int PASSWORD_HISTORY_SIZE = 5;
    private static final int RESET_TOKEN_RATE_LIMIT_HOURS = 1;
    private static final int MAX_RESET_TOKENS_PER_HOUR = 3;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${healthcare.password.min-length:8}")
    private int minLength;

    @Value("${healthcare.password.max-length:128}")
    private int maxLength;

    @Override
    @Transactional
    public void forgotPassword(String email, String ipAddress, String userAgent) {
        log.debug("Processing forgot password request for: {}", email);

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {

            log.debug("No user found for email: {}", email);
            return;
        }

        Instant since = Instant.now().minus(Duration.ofHours(RESET_TOKEN_RATE_LIMIT_HOURS));
        long recentTokens = resetTokenRepository.countRecentTokens(user.getId(), since);

        if (recentTokens >= MAX_RESET_TOKENS_PER_HOUR) {
            log.warn("Password reset rate limit exceeded for user: {}", user.getId());
            throw AuthenticationException.passwordResetRateLimited();
        }

        resetTokenRepository.invalidateUserTokens(user.getId());

        PasswordResetToken.TokenWithHash tokenWithHash =
            PasswordResetToken.create(user, ipAddress, userAgent);
        resetTokenRepository.save(tokenWithHash.entity());

        log.info("Password reset token generated for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        log.debug("Processing password reset");

        PasswordStrength strength = validatePasswordStrength(newPassword);
        if (!strength.valid()) {
            throw new IllegalArgumentException(strength.message());
        }

        String tokenHash = PasswordResetToken.hash(token);
        PasswordResetToken resetToken = resetTokenRepository
            .findValidToken(tokenHash, Instant.now())
            .orElseThrow(AuthenticationException::passwordResetTokenInvalid);

        User user = resetToken.getUser();

        if (wasPasswordUsedRecently(user.getId(), newPassword)) {
            throw AuthenticationException.passwordReused();
        }

        passwordHistoryRepository.save(
            PasswordHistory.forgotPassword(user, user.getPasswordHash())
        );

        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        resetToken.markAsUsed();
        resetTokenRepository.save(resetToken);

        refreshTokenRepository.revokeAllUserTokens(user.getId(), Instant.now());

        eventPublisher.publishEvent(PasswordChangedEvent.forgotPassword(user.getId(), user.getUsername()));

        log.info("Password reset completed for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        log.debug("Processing password change for user: {}", userId);

        User user = findUserOrThrow(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        PasswordStrength strength = validatePasswordStrength(newPassword);
        if (!strength.valid()) {
            throw new IllegalArgumentException(strength.message());
        }

        if (wasPasswordUsedRecently(userId, newPassword)) {
            throw AuthenticationException.passwordReused();
        }

        passwordHistoryRepository.save(
            PasswordHistory.userChanged(user, user.getPasswordHash())
        );

        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.changePassword(newPasswordHash);
        userRepository.save(user);

        eventPublisher.publishEvent(PasswordChangedEvent.userChanged(userId, user.getUsername()));

        log.info("Password changed for user: {}", userId);
    }

    @Override
    @Transactional
    public void adminResetPassword(UUID userId, UUID adminUserId, boolean requireChange) {
        log.debug("Admin {} resetting password for user: {}", adminUserId, userId);

        User user = findUserOrThrow(userId);

        String tempPassword = generateTemporaryPassword();
        String newPasswordHash = passwordEncoder.encode(tempPassword);

        passwordHistoryRepository.save(
            PasswordHistory.adminReset(user, user.getPasswordHash())
        );

        user.changePassword(newPasswordHash);
        if (requireChange) {
            user.requirePasswordChange();
        }
        userRepository.save(user);

        refreshTokenRepository.revokeAllUserTokens(userId, Instant.now());

        eventPublisher.publishEvent(PasswordChangedEvent.adminReset(userId, user.getUsername()));

        log.info("Admin {} reset password for user: {}", adminUserId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean wasPasswordUsedRecently(UUID userId, String password) {
        List<PasswordHistory> recentPasswords =
            passwordHistoryRepository.findRecentByUserId(userId, PASSWORD_HISTORY_SIZE);

        for (PasswordHistory history : recentPasswords) {
            if (passwordEncoder.matches(password, history.getPasswordHash())) {
                return true;
            }
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return true;
        }

        return false;
    }

    @Override
    public PasswordStrength validatePasswordStrength(String password) {
        if (password == null || password.length() < minLength) {
            return PasswordStrength.invalid("Password must be at least " + minLength + " characters");
        }

        if (password.length() > maxLength) {
            return PasswordStrength.invalid("Password must be less than " + maxLength + " characters");
        }

        int score = 0;

        if (UPPERCASE_PATTERN.matcher(password).find()) score++;
        if (LOWERCASE_PATTERN.matcher(password).find()) score++;
        if (DIGIT_PATTERN.matcher(password).find()) score++;
        if (SPECIAL_PATTERN.matcher(password).find()) score++;
        if (password.length() >= 12) score++;

        if (score < 3) {
            return PasswordStrength.invalid(
                "Password must contain at least one uppercase, one lowercase, and one digit"
            );
        }

        return PasswordStrength.valid(score);
    }

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.byId(userId));
    }

    private String generateTemporaryPassword() {

        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789!@#$%";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}
