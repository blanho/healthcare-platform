package com.healthcare.auth.repository;

import com.healthcare.auth.domain.LoginAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    @Query("""
        SELECT la FROM LoginAttempt la
        WHERE la.username = :username
        AND la.status != 'SUCCESS'
        AND la.attemptedAt > :since
        ORDER BY la.attemptedAt DESC
        """)
    List<LoginAttempt> findRecentFailedAttempts(
        @Param("username") String username,
        @Param("since") Instant since
    );

    @Query("""
        SELECT COUNT(la) FROM LoginAttempt la
        WHERE la.ipAddress = :ipAddress
        AND la.status != 'SUCCESS'
        AND la.attemptedAt > :since
        """)
    long countFailedAttemptsByIp(
        @Param("ipAddress") String ipAddress,
        @Param("since") Instant since
    );

    @Query("""
        SELECT COUNT(la) FROM LoginAttempt la
        WHERE la.username = :username
        AND la.status != 'SUCCESS'
        AND la.attemptedAt > :since
        """)
    long countFailedAttemptsByUsername(
        @Param("username") String username,
        @Param("since") Instant since
    );

    Page<LoginAttempt> findByUserIdOrderByAttemptedAtDesc(UUID userId, Pageable pageable);

    @Query("""
        SELECT la FROM LoginAttempt la
        WHERE la.userId = :userId
        ORDER BY la.attemptedAt DESC
        """)
    List<LoginAttempt> findRecentByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT la FROM LoginAttempt la
        WHERE la.userId = :userId
        AND la.status = 'SUCCESS'
        ORDER BY la.attemptedAt DESC
        """)
    List<LoginAttempt> findSuccessfulLoginsByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("DELETE FROM LoginAttempt la WHERE la.attemptedAt < :before")
    int deleteOlderThan(@Param("before") Instant before);
}
