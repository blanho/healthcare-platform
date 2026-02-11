package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.LoginAttemptResponse;
import com.healthcare.auth.api.dto.SessionResponse;
import com.healthcare.auth.domain.LoginAttempt;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.domain.UserSession;
import com.healthcare.auth.domain.event.UserLoggedOutEvent;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.repository.LoginAttemptRepository;
import com.healthcare.auth.repository.UserRepository;
import com.healthcare.auth.repository.UserSessionRepository;
import com.healthcare.auth.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class SessionServiceImpl implements SessionService {

    private final UserSessionRepository sessionRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<SessionResponse> getActiveSessions(UUID userId, String currentTokenHash) {
        List<UserSession> sessions = sessionRepository.findActiveSessionsByUserId(userId, Instant.now());

        return sessions.stream()
            .map(session -> SessionResponse.from(
                session,
                session.getRefreshTokenHash().equals(currentTokenHash)
            ))
            .toList();
    }

    @Override
    @Transactional
    public void revokeSession(UUID userId, UUID sessionId) {
        log.debug("Revoking session {} for user {}", sessionId, userId);

        UserSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Cannot revoke session belonging to another user");
        }

        session.revoke("User revoked session");
        sessionRepository.save(session);

        log.info("Session {} revoked for user {}", sessionId, userId);
    }

    @Override
    @Transactional
    public void revokeAllOtherSessions(UUID userId, String currentTokenHash) {
        log.debug("Revoking all other sessions for user {}", userId);

        List<UserSession> sessions = sessionRepository.findActiveSessionsByUserId(userId, Instant.now());

        int revokedCount = 0;
        for (UserSession session : sessions) {
            if (!session.getRefreshTokenHash().equals(currentTokenHash)) {
                session.revoke("User revoked all other sessions");
                sessionRepository.save(session);
                revokedCount++;
            }
        }

        log.info("Revoked {} sessions for user {}", revokedCount, userId);
    }

    @Override
    @Transactional
    public void revokeAllSessions(UUID userId, String reason) {
        log.debug("Revoking all sessions for user {}", userId);

        int revokedCount = sessionRepository.revokeAllUserSessions(userId, Instant.now(), reason);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            eventPublisher.publishEvent(UserLoggedOutEvent.allDevices(userId, user.getUsername()));
        }

        log.info("Revoked {} sessions for user {}", revokedCount, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginAttemptResponse> getLoginHistory(UUID userId, int limit) {
        List<LoginAttempt> attempts = loginAttemptRepository.findRecentByUserId(
            userId,
            PageRequest.of(0, limit)
        );

        return attempts.stream()
            .map(LoginAttemptResponse::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public int getActiveSessionCount(UUID userId) {
        return sessionRepository.countActiveSessionsByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    public void recordSession(UUID userId, String refreshTokenHash, String ipAddress, String userAgent) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.byId(userId));

        Instant expiresAt = Instant.now().plusSeconds(7 * 24 * 3600);

        UserSession session = new UserSession(user, refreshTokenHash, ipAddress, userAgent, expiresAt);
        sessionRepository.save(session);

        log.debug("Recorded new session for user {}", userId);
    }

    @Override
    @Transactional
    public void updateSessionActivity(String refreshTokenHash) {
        sessionRepository.findByRefreshTokenHash(refreshTokenHash)
            .ifPresent(session -> {
                session.recordActivity();
                sessionRepository.save(session);
            });
    }
}
