package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.Invoice;
import com.healthcare.billing.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceSummaryResponse(
    UUID id,
    String invoiceNumber,
    UUID patientId,
    BigDecimal totalAmount,
    BigDecimal balanceDue,
    LocalDate invoiceDate,
    LocalDate dueDate,
    InvoiceStatus status,
    int itemCount,
    boolean hasInsuranceClaim
) {
    public static InvoiceSummaryResponse from(Invoice invoice) {
        return new InvoiceSummaryResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getPatientId(),
            invoice.getTotalAmount(),
            invoice.getBalanceDue(),
            invoice.getInvoiceDate(),
            invoice.getDueDate(),
            invoice.getStatus(),
            invoice.getItems().size(),
            invoice.getInsuranceClaimNumber() != null
        );
    }
}
