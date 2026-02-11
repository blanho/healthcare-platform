package com.healthcare.billing.repository;

import com.healthcare.billing.domain.ClaimStatus;
import com.healthcare.billing.domain.InsuranceClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, UUID> {

    Optional<InsuranceClaim> findByClaimNumber(String claimNumber);

    boolean existsByClaimNumber(String claimNumber);

    Optional<InsuranceClaim> findByInvoiceId(UUID invoiceId);

    Page<InsuranceClaim> findByPatientId(UUID patientId, Pageable pageable);

    Page<InsuranceClaim> findByStatus(ClaimStatus status, Pageable pageable);

    Page<InsuranceClaim> findByInsuranceProvider(String insuranceProvider, Pageable pageable);

    @Query("""
        SELECT c FROM InsuranceClaim c
        WHERE c.status IN :statuses
        ORDER BY c.submittedAt DESC
        """)
    Page<InsuranceClaim> findByStatuses(@Param("statuses") List<ClaimStatus> statuses,
                                         Pageable pageable);

    @Query("""
        SELECT c FROM InsuranceClaim c
        WHERE c.status = 'SUBMITTED'
        AND c.submittedAt < :cutoffDate
        """)
    List<InsuranceClaim> findPendingClaimsOlderThan(@Param("cutoffDate") Instant cutoffDate);

    @Query("""
        SELECT c FROM InsuranceClaim c
        WHERE c.insuranceProvider = :provider
        AND c.status = :status
        """)
    Page<InsuranceClaim> findByProviderAndStatus(@Param("provider") String provider,
                                                  @Param("status") ClaimStatus status,
                                                  Pageable pageable);

    @Query("""
        SELECT c FROM InsuranceClaim c
        WHERE c.patientId = :patientId
        AND c.status IN ('SUBMITTED', 'ACKNOWLEDGED', 'IN_REVIEW', 'PENDING_INFO')
        """)
    List<InsuranceClaim> findPendingClaimsForPatient(@Param("patientId") UUID patientId);

    @Query("""
        SELECT SUM(c.paidAmount) FROM InsuranceClaim c
        WHERE c.processedAt BETWEEN :startDate AND :endDate
        AND c.status IN ('APPROVED', 'PARTIALLY_APPROVED', 'PAID')
        """)
    Optional<BigDecimal> calculateInsuranceRevenueInPeriod(@Param("startDate") Instant startDate,
                                                            @Param("endDate") Instant endDate);

    @Query("""
        SELECT c.insuranceProvider, COUNT(c), SUM(c.billedAmount), SUM(c.paidAmount) FROM InsuranceClaim c
        WHERE c.processedAt BETWEEN :startDate AND :endDate
        AND c.status IN ('APPROVED', 'PARTIALLY_APPROVED', 'PAID', 'DENIED')
        GROUP BY c.insuranceProvider
        """)
    List<Object[]> getClaimBreakdownByProvider(@Param("startDate") Instant startDate,
                                                @Param("endDate") Instant endDate);

    @Query("""
        SELECT c.status, COUNT(c) FROM InsuranceClaim c
        WHERE c.submittedAt BETWEEN :startDate AND :endDate
        GROUP BY c.status
        """)
    List<Object[]> getClaimStatusBreakdown(@Param("startDate") Instant startDate,
                                            @Param("endDate") Instant endDate);

    @Query("""
        SELECT COUNT(c) FROM InsuranceClaim c
        WHERE c.status = 'DENIED'
        AND c.submittedAt BETWEEN :startDate AND :endDate
        """)
    long countDeniedClaimsInPeriod(@Param("startDate") Instant startDate,
                                    @Param("endDate") Instant endDate);
}
