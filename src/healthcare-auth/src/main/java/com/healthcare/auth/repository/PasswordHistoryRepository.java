package com.healthcare.auth.repository;

import com.healthcare.auth.domain.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, UUID> {

    @Query("""
        SELECT ph FROM PasswordHistory ph
        WHERE ph.user.id = :userId
        ORDER BY ph.createdAt DESC
        LIMIT :limit
        """)
    List<PasswordHistory> findRecentByUserId(
        @Param("userId") UUID userId,
        @Param("limit") int limit
    );

    long countByUserId(UUID userId);

    @Query("""
        DELETE FROM PasswordHistory ph
        WHERE ph.user.id = :userId
        AND ph.id NOT IN (
            SELECT ph2.id FROM PasswordHistory ph2
            WHERE ph2.user.id = :userId
            ORDER BY ph2.createdAt DESC
            LIMIT :keep
        )
        """)
    int deleteOldHistory(@Param("userId") UUID userId, @Param("keep") int keep);
}
