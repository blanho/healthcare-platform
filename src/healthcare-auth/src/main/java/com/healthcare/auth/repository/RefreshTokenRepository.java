package com.healthcare.auth.repository;

import com.healthcare.auth.domain.RefreshToken;
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
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Query("SELECT t FROM RefreshToken t WHERE t.tokenHash = :tokenHash AND t.revoked = false AND t.expiresAt > :now")
    Optional<RefreshToken> findValidToken(@Param("tokenHash") String tokenHash, @Param("now") Instant now);

    List<RefreshToken> findByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("SELECT t FROM RefreshToken t WHERE t.user.id = :userId AND t.revoked = false AND t.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true, t.revokedAt = :now WHERE t.user.id = :userId AND t.revoked = false")
    int revokeAllUserTokens(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :cutoff")
    int deleteExpiredTokens(@Param("cutoff") Instant cutoff);

    @Query("SELECT COUNT(t) FROM RefreshToken t WHERE t.user.id = :userId AND t.revoked = false AND t.expiresAt > :now")
    long countActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") Instant now);
}
