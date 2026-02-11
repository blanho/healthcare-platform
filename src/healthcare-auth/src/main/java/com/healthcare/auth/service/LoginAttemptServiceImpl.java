package com.healthcare.auth.service;

import com.healthcare.auth.domain.LoginAttempt;
import com.healthcare.auth.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Value("${healthcare.security.max-ip-failures:20}")
    private int maxIpFailures;

    @Value("${healthcare.security.max-username-failures:5}")
    private int maxUsernameFailures;

    @Value("${healthcare.security.lockout-window-minutes:15}")
    private int lockoutWindowMinutes;

    @Value("${healthcare.security.captcha-threshold:3}")
    private int captchaThreshold;

    @Override
    @Transactional
    public void recordSuccess(UUID userId, String username, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.success(userId, username, ipAddress, userAgent);
        loginAttemptRepository.save(attempt);
        log.debug("Recorded successful login for user: {}", userId);
    }

    @Override
    @Transactional
    public void recordFailure(
            String username,
            String ipAddress,
            String userAgent,
            LoginAttempt.Status status,
            String reason
    ) {
        LoginAttempt attempt = LoginAttempt.failed(username, ipAddress, userAgent, status, reason);
        loginAttemptRepository.save(attempt);
        log.debug("Recorded failed login attempt for username: {}", username);
    }

    @Override
    @Transactional
    public void recordFailureWithUser(
            UUID userId,
            String username,
            String ipAddress,
            String userAgent,
            LoginAttempt.Status status,
            String reason
    ) {
        LoginAttempt attempt = LoginAttempt.failedWithUser(userId, username, ipAddress, userAgent, status, reason);
        loginAttemptRepository.save(attempt);
        log.debug("Recorded failed login attempt for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isIpBlocked(String ipAddress) {
        Instant since = Instant.now().minus(Duration.ofMinutes(lockoutWindowMinutes));
        long failedCount = loginAttemptRepository.countFailedAttemptsByIp(ipAddress, since);

        if (failedCount >= maxIpFailures) {
            log.warn("IP address blocked due to excessive failures: {}", ipAddress);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUsernameBlocked(String username) {
        Instant since = Instant.now().minus(Duration.ofMinutes(lockoutWindowMinutes));
        long failedCount = loginAttemptRepository.countFailedAttemptsByUsername(username, since);

        if (failedCount >= maxUsernameFailures) {
            log.warn("Username blocked due to excessive failures: {}", username);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean shouldRequireCaptcha(String ipAddress, String username) {
        Instant since = Instant.now().minus(Duration.ofMinutes(lockoutWindowMinutes));

        long ipFailures = loginAttemptRepository.countFailedAttemptsByIp(ipAddress, since);
        if (ipFailures >= captchaThreshold) {
            return true;
        }

        if (username != null) {
            long usernameFailures = loginAttemptRepository.countFailedAttemptsByUsername(username, since);
            if (usernameFailures >= captchaThreshold) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public long getRecentFailedAttemptCount(String username) {
        Instant since = Instant.now().minus(Duration.ofMinutes(lockoutWindowMinutes));
        return loginAttemptRepository.countFailedAttemptsByUsername(username, since);
    }
}
