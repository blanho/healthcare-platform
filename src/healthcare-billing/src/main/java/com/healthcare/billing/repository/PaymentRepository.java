package com.healthcare.billing.repository;

import com.healthcare.billing.domain.Payment;
import com.healthcare.billing.domain.PaymentMethod;
import com.healthcare.billing.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    List<Payment> findByInvoiceId(UUID invoiceId);

    Page<Payment> findByPatientId(UUID patientId, Pageable pageable);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    @Query("""
        SELECT p FROM Payment p
        WHERE p.invoiceId = :invoiceId
        AND p.status = 'COMPLETED'
        """)
    List<Payment> findCompletedPaymentsByInvoice(@Param("invoiceId") UUID invoiceId);

    @Query("""
        SELECT SUM(p.amount) FROM Payment p
        WHERE p.invoiceId = :invoiceId
        AND p.status = 'COMPLETED'
        """)
    Optional<BigDecimal> calculateTotalPaidForInvoice(@Param("invoiceId") UUID invoiceId);

    @Query("""
        SELECT p FROM Payment p
        WHERE p.paymentDate BETWEEN :startDate AND :endDate
        AND p.status = 'COMPLETED'
        ORDER BY p.paymentDate DESC
        """)
    Page<Payment> findPaymentsInDateRange(@Param("startDate") Instant startDate,
                                           @Param("endDate") Instant endDate,
                                           Pageable pageable);

    @Query("""
        SELECT p FROM Payment p
        WHERE p.patientId = :patientId
        AND p.status = 'COMPLETED'
        ORDER BY p.paymentDate DESC
        """)
    List<Payment> findPatientPaymentHistory(@Param("patientId") UUID patientId);

    @Query("""
        SELECT SUM(p.amount) FROM Payment p
        WHERE p.paymentDate BETWEEN :startDate AND :endDate
        AND p.status = 'COMPLETED'
        """)
    Optional<BigDecimal> calculateRevenueInPeriod(@Param("startDate") Instant startDate,
                                                   @Param("endDate") Instant endDate);

    @Query("""
        SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p
        WHERE p.paymentDate BETWEEN :startDate AND :endDate
        AND p.status = 'COMPLETED'
        GROUP BY p.paymentMethod
        """)
    List<Object[]> getPaymentMethodBreakdown(@Param("startDate") Instant startDate,
                                              @Param("endDate") Instant endDate);
}
