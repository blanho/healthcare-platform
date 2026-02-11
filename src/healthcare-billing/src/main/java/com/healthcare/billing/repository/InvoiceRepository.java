package com.healthcare.billing.repository;

import com.healthcare.billing.domain.Invoice;
import com.healthcare.billing.domain.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByPatientId(UUID patientId, Pageable pageable);

    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);

    Page<Invoice> findByPatientIdAndStatus(UUID patientId, InvoiceStatus status, Pageable pageable);

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.status = :status
        AND i.dueDate < :date
        """)
    List<Invoice> findOverdueInvoices(@Param("status") InvoiceStatus status,
                                       @Param("date") LocalDate date);

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.status IN :statuses
        AND i.dueDate BETWEEN :startDate AND :endDate
        """)
    Page<Invoice> findByStatusesAndDateRange(@Param("statuses") List<InvoiceStatus> statuses,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate,
                                              Pageable pageable);

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.patientId = :patientId
        AND i.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')
        ORDER BY i.dueDate ASC
        """)
    List<Invoice> findUnpaidByPatient(@Param("patientId") UUID patientId);

    @Query("""
        SELECT i FROM Invoice i
        WHERE i.appointmentId = :appointmentId
        """)
    Optional<Invoice> findByAppointmentId(@Param("appointmentId") UUID appointmentId);

    @Query("""
        SELECT SUM(i.balanceDue) FROM Invoice i
        WHERE i.patientId = :patientId
        AND i.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')
        """)
    Optional<java.math.BigDecimal> calculatePatientBalance(@Param("patientId") UUID patientId);

    @Query("""
        SELECT COUNT(i) FROM Invoice i
        WHERE i.status = 'OVERDUE'
        AND i.dueDate < :date
        """)
    long countOverdue(@Param("date") LocalDate date);

    @Query(value = """
        SELECT i.* FROM invoices i
        WHERE i.invoice_date BETWEEN :startDate AND :endDate
        ORDER BY i.invoice_date DESC
        """, nativeQuery = true)
    Page<Invoice> findByInvoiceDateRange(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate,
                                          Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i
        WHERE i.status IN ('PAID', 'PARTIALLY_PAID')
        AND i.invoiceDate BETWEEN :startDate AND :endDate
        """)
    java.math.BigDecimal sumRevenueForPeriod(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COALESCE(SUM(i.paidAmount), 0) FROM Invoice i
        WHERE i.invoiceDate BETWEEN :startDate AND :endDate
        """)
    java.math.BigDecimal sumCollectionsForPeriod(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COALESCE(SUM(i.balanceDue), 0) FROM Invoice i
        WHERE i.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')
        AND i.invoiceDate BETWEEN :startDate AND :endDate
        """)
    java.math.BigDecimal sumOutstandingForPeriod(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT COALESCE(SUM(i.balanceDue), 0) FROM Invoice i
        WHERE i.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')
        """)
    java.math.BigDecimal sumTotalOutstanding();

    long countByStatus(InvoiceStatus status);
}
