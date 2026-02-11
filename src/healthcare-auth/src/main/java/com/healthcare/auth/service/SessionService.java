package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.LoginAttemptResponse;
import com.healthcare.auth.api.dto.SessionResponse;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    List<SessionResponse> getActiveSessions(UUID userId, String currentTokenHash);

    void revokeSession(UUID userId, UUID sessionId);

    void revokeAllOtherSessions(UUID userId, String currentTokenHash);

    void revokeAllSessions(UUID userId, String reason);

    List<LoginAttemptResponse> getLoginHistory(UUID userId, int limit);

    int getActiveSessionCount(UUID userId);

    void recordSession(UUID userId, String refreshTokenHash, String ipAddress, String userAgent);

    void updateSessionActivity(String refreshTokenHash);
}
