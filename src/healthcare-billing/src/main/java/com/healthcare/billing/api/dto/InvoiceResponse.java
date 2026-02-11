package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.Invoice;
import com.healthcare.billing.domain.InvoiceItem;
import com.healthcare.billing.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
    UUID id,
    String invoiceNumber,
    UUID patientId,
    UUID appointmentId,
    BigDecimal subtotal,
    BigDecimal taxAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal balanceDue,
    LocalDate invoiceDate,
    LocalDate dueDate,
    LocalDate paidDate,
    InvoiceStatus status,
    String insuranceClaimNumber,
    BigDecimal insuranceAmount,
    String notes,
    List<InvoiceItemResponse> items,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {
    public record InvoiceItemResponse(
        UUID id,
        String description,
        String procedureCode,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
    ) {
        public static InvoiceItemResponse from(InvoiceItem item) {
            return new InvoiceItemResponse(
                item.getId(),
                item.getDescription(),
                item.getProcedureCode(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            );
        }
    }

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
            invoice.getId(),
            invoice.getInvoiceNumber(),
            invoice.getPatientId(),
            invoice.getAppointmentId(),
            invoice.getSubtotal(),
            invoice.getTaxAmount(),
            invoice.getDiscountAmount(),
            invoice.getTotalAmount(),
            invoice.getPaidAmount(),
            invoice.getBalanceDue(),
            invoice.getInvoiceDate(),
            invoice.getDueDate(),
            invoice.getPaidDate(),
            invoice.getStatus(),
            invoice.getInsuranceClaimNumber(),
            invoice.getInsuranceAmount(),
            invoice.getNotes(),
            invoice.getItems().stream()
                .map(InvoiceItemResponse::from)
                .toList(),
            invoice.getCreatedAt(),
            invoice.getUpdatedAt(),
            invoice.getCreatedBy()
        );
    }
}
