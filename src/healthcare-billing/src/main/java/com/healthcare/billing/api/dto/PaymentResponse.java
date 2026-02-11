package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.Payment;
import com.healthcare.billing.domain.PaymentMethod;
import com.healthcare.billing.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
    UUID id,
    String referenceNumber,
    UUID invoiceId,
    UUID patientId,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String transactionId,
    String authorizationCode,
    String cardLastFour,
    String cardBrand,
    String failureReason,
    String notes,
    Instant paymentDate,
    Instant processedAt,
    Instant refundedAt,
    BigDecimal refundAmount,
    Instant createdAt,
    String createdBy
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getReferenceNumber(),
            payment.getInvoiceId(),
            payment.getPatientId(),
            payment.getAmount(),
            payment.getPaymentMethod(),
            payment.getStatus(),
            payment.getTransactionId(),
            payment.getAuthorizationCode(),
            payment.getCardLastFour(),
            payment.getCardBrand(),
            payment.getFailureReason(),
            payment.getNotes(),
            payment.getPaymentDate(),
            payment.getProcessedAt(),
            payment.getRefundedAt(),
            payment.getRefundAmount(),
            payment.getCreatedAt(),
            payment.getCreatedBy()
        );
    }
}
