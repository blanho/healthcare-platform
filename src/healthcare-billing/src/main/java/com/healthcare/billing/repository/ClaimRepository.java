package com.healthcare.billing.repository;

import com.healthcare.billing.domain.Claim;
import com.healthcare.billing.domain.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, UUID> {

    Optional<Claim> findByClaimNumber(String claimNumber);

    Optional<Claim> findByInvoiceId(UUID invoiceId);

    Page<Claim> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);

    Page<Claim> findByStatusOrderByCreatedAtDesc(ClaimStatus status, Pageable pageable);

    @Query("SELECT c FROM Claim c WHERE c.status IN :statuses ORDER BY c.createdAt DESC")
    Page<Claim> findPendingClaims(
        @Param("statuses") java.util.List<ClaimStatus> statuses,
        Pageable pageable
    );

    boolean existsByInvoiceId(UUID invoiceId);
}
