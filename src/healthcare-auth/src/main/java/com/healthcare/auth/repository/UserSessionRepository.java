package com.healthcare.auth.repository;

import com.healthcare.auth.domain.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    @Query("""
        SELECT s FROM UserSession s
        WHERE s.user.id = :userId
        AND s.revoked = false
        AND s.expiresAt > :now
        ORDER BY s.lastActivityAt DESC
        """)
    List<UserSession> findActiveSessionsByUserId(
        @Param("userId") UUID userId,
        @Param("now") Instant now
    );

    @Modifying
    @Query("""
        UPDATE UserSession s
        SET s.revoked = true, s.revokedAt = :now, s.revokedReason = :reason
        WHERE s.user.id = :userId AND s.revoked = false
        """)
    int revokeAllUserSessions(
        @Param("userId") UUID userId,
        @Param("now") Instant now,
        @Param("reason") String reason
    );

    @Query("""
        SELECT COUNT(s) FROM UserSession s
        WHERE s.user.id = :userId
        AND s.revoked = false
        AND s.expiresAt > :now
        """)
    int countActiveSessionsByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("""
        SELECT s FROM UserSession s
        WHERE s.expiresAt < :now OR s.revoked = true
        """)
    List<UserSession> findExpiredOrRevokedSessions(@Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.revoked = true AND s.revokedAt < :before")
    int deleteRevokedSessionsOlderThan(@Param("before") Instant before);
}
