package com.healthcare.billing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_invoice", columnList = "invoice_id"),
    @Index(name = "idx_payment_patient", columnList = "patient_id"),
    @Index(name = "idx_payment_reference", columnList = "reference_number"),
    @Index(name = "idx_payment_status", columnList = "status")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    @NotNull
    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "authorization_code", length = 50)
    private String authorizationCode;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 20)
    private String cardBrand;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "refunded_at")
    private Instant refundedAt;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private String createdBy;

    protected Payment() {

    }

    private Payment(Builder builder) {
        this.referenceNumber = builder.referenceNumber;
        this.invoiceId = builder.invoiceId;
        this.patientId = builder.patientId;
        this.amount = builder.amount;
        this.paymentMethod = builder.paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.notes = builder.notes;
        this.paymentDate = Instant.now();
        this.createdAt = Instant.now();
        this.createdBy = builder.createdBy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void markProcessing() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment must be pending to process");
        }
        this.status = PaymentStatus.PROCESSING;
    }

    public void complete(String transactionId, String authorizationCode) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.authorizationCode = authorizationCode;
        this.processedAt = Instant.now();
    }

    public void recordCardDetails(String lastFour, String brand) {
        this.cardLastFour = lastFour;
        this.cardBrand = brand;
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.processedAt = Instant.now();
    }

    public void cancel() {
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Cannot cancel payment in status: " + this.status);
        }
        this.status = PaymentStatus.CANCELLED;
    }

    public void refund(BigDecimal refundAmount) {
        if (!this.status.canRefund()) {
            throw new IllegalStateException("Cannot refund payment in status: " + this.status);
        }
        if (refundAmount.compareTo(this.amount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }

        this.refundAmount = refundAmount;
        this.refundedAt = Instant.now();

        if (refundAmount.compareTo(this.amount) == 0) {
            this.status = PaymentStatus.REFUNDED;
        }
    }

    public boolean isSuccessful() {
        return this.status.isSuccessful();
    }

    public UUID getId() { return id; }
    public String getReferenceNumber() { return referenceNumber; }
    public UUID getInvoiceId() { return invoiceId; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getAuthorizationCode() { return authorizationCode; }
    public String getCardLastFour() { return cardLastFour; }
    public String getCardBrand() { return cardBrand; }
    public String getFailureReason() { return failureReason; }
    public String getNotes() { return notes; }
    public Instant getPaymentDate() { return paymentDate; }
    public Instant getProcessedAt() { return processedAt; }
    public Instant getRefundedAt() { return refundedAt; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }

    public static class Builder {
        private String referenceNumber;
        private UUID invoiceId;
        private UUID patientId;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private String notes;
        private String createdBy;

        public Builder referenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
            return this;
        }

        public Builder invoiceId(UUID invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Payment build() {
            if (referenceNumber == null || referenceNumber.isBlank()) {
                throw new IllegalStateException("Reference number is required");
            }
            if (invoiceId == null) {
                throw new IllegalStateException("Invoice ID is required");
            }
            if (patientId == null) {
                throw new IllegalStateException("Patient ID is required");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Payment amount must be positive");
            }
            if (paymentMethod == null) {
                throw new IllegalStateException("Payment method is required");
            }
            return new Payment(this);
        }
    }
}
