package com.healthcare.auth.api;

import com.healthcare.auth.api.dto.LoginAttemptResponse;
import com.healthcare.auth.api.dto.SessionResponse;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/sessions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sessions", description = "Session management operations")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @Operation(summary = "Get active sessions", description = "Get all active sessions for current user")
    public ResponseEntity<List<SessionResponse>> getActiveSessions(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(value = "X-Token-Hash", required = false) String currentTokenHash
    ) {
        log.debug("Getting active sessions for user: {}", user.getId());

        List<SessionResponse> sessions = sessionService.getActiveSessions(user.getId(), currentTokenHash);

        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{sessionId}")
    @Operation(summary = "Revoke session", description = "Revoke a specific session")
    public ResponseEntity<Void> revokeSession(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId
    ) {
        log.debug("Revoking session {} for user: {}", sessionId, user.getId());

        sessionService.revokeSession(user.getId(), sessionId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/others")
    @Operation(summary = "Revoke other sessions", description = "Revoke all sessions except current")
    public ResponseEntity<Map<String, String>> revokeOtherSessions(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestHeader(value = "X-Token-Hash", required = false) String currentTokenHash
    ) {
        log.debug("Revoking all other sessions for user: {}", user.getId());

        sessionService.revokeAllOtherSessions(user.getId(), currentTokenHash);

        return ResponseEntity.ok(Map.of("message", "All other sessions have been revoked"));
    }

    @DeleteMapping("/all")
    @Operation(summary = "Revoke all sessions", description = "Revoke all sessions including current")
    public ResponseEntity<Map<String, String>> revokeAllSessions(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        log.debug("Revoking all sessions for user: {}", user.getId());

        sessionService.revokeAllSessions(user.getId(), "User requested logout from all devices");

        return ResponseEntity.ok(Map.of("message", "All sessions have been revoked"));
    }

    @GetMapping("/count")
    @Operation(summary = "Get session count", description = "Get number of active sessions")
    public ResponseEntity<Map<String, Integer>> getSessionCount(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        int count = sessionService.getActiveSessionCount(user.getId());

        return ResponseEntity.ok(Map.of("activeSessions", count));
    }

    @GetMapping("/history")
    @Operation(summary = "Get login history", description = "Get recent login attempts")
    public ResponseEntity<List<LoginAttemptResponse>> getLoginHistory(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "20") int limit
    ) {
        log.debug("Getting login history for user: {}", user.getId());

        List<LoginAttemptResponse> history = sessionService.getLoginHistory(user.getId(), limit);

        return ResponseEntity.ok(history);
    }
}
