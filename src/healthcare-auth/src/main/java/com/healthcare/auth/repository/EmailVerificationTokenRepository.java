package com.healthcare.auth.repository;

import com.healthcare.auth.domain.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    @Query("""
        SELECT t FROM EmailVerificationToken t
        WHERE t.tokenHash = :tokenHash
        AND t.used = false
        AND t.expiresAt > :now
        """)
    Optional<EmailVerificationToken> findValidToken(
        @Param("tokenHash") String tokenHash,
        @Param("now") Instant now
    );

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        UPDATE EmailVerificationToken t
        SET t.used = true
        WHERE t.user.id = :userId AND t.used = false
        """)
    int invalidateUserTokens(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :before")
    int deleteExpiredTokens(@Param("before") Instant before);
}
