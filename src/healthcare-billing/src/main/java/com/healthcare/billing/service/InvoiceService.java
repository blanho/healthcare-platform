package com.healthcare.billing.service;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.Invoice;
import com.healthcare.billing.domain.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest request, String createdBy);

    InvoiceResponse getInvoice(UUID invoiceId);

    InvoiceResponse getInvoiceByNumber(String invoiceNumber);

    Page<InvoiceSummaryResponse> getPatientInvoices(UUID patientId, Pageable pageable);

    Page<InvoiceSummaryResponse> getInvoicesByStatus(InvoiceStatus status, Pageable pageable);

    List<InvoiceSummaryResponse> getOverdueInvoices();

    InvoiceResponse addItem(UUID invoiceId, AddInvoiceItemRequest request, String updatedBy);

    InvoiceResponse removeItem(UUID invoiceId, UUID itemId, String updatedBy);

    InvoiceResponse applyDiscount(UUID invoiceId, BigDecimal amount, String updatedBy);

    InvoiceResponse applyPercentageDiscount(UUID invoiceId, BigDecimal percentage, String updatedBy);

    InvoiceResponse finalizeInvoice(UUID invoiceId, String updatedBy);

    InvoiceResponse cancelInvoice(UUID invoiceId, String updatedBy);

    BigDecimal getPatientBalance(UUID patientId);

    InvoiceResponse getInvoiceForAppointment(UUID appointmentId);

    int markOverdueInvoices();

    Page<InvoiceSummaryResponse> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    BigDecimal getRevenueForPeriod(LocalDate startDate, LocalDate endDate);

    BigDecimal getCollectionsForPeriod(LocalDate startDate, LocalDate endDate);

    BigDecimal getOutstandingForPeriod(LocalDate startDate, LocalDate endDate);

    BigDecimal getTotalOutstanding();

    long countByStatus(InvoiceStatus status);
}
