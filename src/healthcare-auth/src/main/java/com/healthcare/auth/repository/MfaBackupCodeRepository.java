package com.healthcare.auth.repository;

import com.healthcare.auth.domain.MfaBackupCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MfaBackupCodeRepository extends JpaRepository<MfaBackupCode, UUID> {

    @Query("SELECT c FROM MfaBackupCode c WHERE c.user.id = :userId AND c.used = false")
    List<MfaBackupCode> findUnusedByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(c) FROM MfaBackupCode c WHERE c.user.id = :userId AND c.used = false")
    int countUnusedByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM MfaBackupCode c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
