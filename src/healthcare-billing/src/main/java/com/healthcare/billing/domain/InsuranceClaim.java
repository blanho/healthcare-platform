package com.healthcare.billing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insurance_claims", indexes = {
    @Index(name = "idx_claim_number", columnList = "claim_number", unique = true),
    @Index(name = "idx_claim_invoice", columnList = "invoice_id"),
    @Index(name = "idx_claim_patient", columnList = "patient_id"),
    @Index(name = "idx_claim_status", columnList = "status")
})
public class InsuranceClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "claim_number", nullable = false, unique = true, length = 50)
    private String claimNumber;

    @NotNull
    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @NotBlank
    @Column(name = "insurance_provider", nullable = false)
    private String insuranceProvider;

    @NotBlank
    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    @Column(name = "group_number", length = 50)
    private String groupNumber;

    @Column(name = "subscriber_name")
    private String subscriberName;

    @Column(name = "subscriber_id", length = 50)
    private String subscriberId;

    @NotNull
    @Column(name = "billed_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal billedAmount;

    @Column(name = "allowed_amount", precision = 12, scale = 2)
    private BigDecimal allowedAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "patient_responsibility", precision = 12, scale = 2)
    private BigDecimal patientResponsibility;

    @Column(name = "copay_amount", precision = 12, scale = 2)
    private BigDecimal copayAmount;

    @Column(name = "deductible_amount", precision = 12, scale = 2)
    private BigDecimal deductibleAmount;

    @Column(name = "coinsurance_amount", precision = 12, scale = 2)
    private BigDecimal coinsuranceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ClaimStatus status;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "denial_reason", length = 500)
    private String denialReason;

    @Column(name = "denial_code", length = 20)
    private String denialCode;

    @Column(name = "adjudication_notes", columnDefinition = "TEXT")
    private String adjudicationNotes;

    @Column(name = "eob_reference", length = 100)
    private String eobReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    protected InsuranceClaim() {

    }

    private InsuranceClaim(Builder builder) {
        this.claimNumber = builder.claimNumber;
        this.invoiceId = builder.invoiceId;
        this.patientId = builder.patientId;
        this.insuranceProvider = builder.insuranceProvider;
        this.policyNumber = builder.policyNumber;
        this.groupNumber = builder.groupNumber;
        this.subscriberName = builder.subscriberName;
        this.subscriberId = builder.subscriberId;
        this.billedAmount = builder.billedAmount;
        this.serviceDate = builder.serviceDate;
        this.status = ClaimStatus.DRAFT;
        this.paidAmount = BigDecimal.ZERO;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.createdBy = builder.createdBy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void submit() {
        if (!this.status.canSubmit()) {
            throw new IllegalStateException("Claim cannot be submitted in status: " + this.status);
        }
        this.status = ClaimStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void acknowledge() {
        if (this.status != ClaimStatus.SUBMITTED) {
            throw new IllegalStateException("Claim must be submitted to be acknowledged");
        }
        this.status = ClaimStatus.ACKNOWLEDGED;
        this.updatedAt = Instant.now();
    }

    public void markInReview() {
        this.status = ClaimStatus.IN_REVIEW;
        this.updatedAt = Instant.now();
    }

    public void requestInfo(String notes) {
        this.status = ClaimStatus.PENDING_INFO;
        this.adjudicationNotes = notes;
        this.updatedAt = Instant.now();
    }

    public void approve(BigDecimal allowedAmount, BigDecimal paidAmount, BigDecimal patientResponsibility) {
        this.status = ClaimStatus.APPROVED;
        this.allowedAmount = allowedAmount;
        this.paidAmount = paidAmount;
        this.patientResponsibility = patientResponsibility;
        this.processedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void partiallyApprove(BigDecimal allowedAmount, BigDecimal paidAmount,
                                  BigDecimal patientResponsibility, String notes) {
        this.status = ClaimStatus.PARTIALLY_APPROVED;
        this.allowedAmount = allowedAmount;
        this.paidAmount = paidAmount;
        this.patientResponsibility = patientResponsibility;
        this.adjudicationNotes = notes;
        this.processedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void deny(String denialCode, String denialReason) {
        this.status = ClaimStatus.DENIED;
        this.denialCode = denialCode;
        this.denialReason = denialReason;
        this.paidAmount = BigDecimal.ZERO;
        this.processedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void appeal(String appealNotes) {
        if (!this.status.canAppeal()) {
            throw new IllegalStateException("Claim cannot be appealed in status: " + this.status);
        }
        this.status = ClaimStatus.APPEALED;
        this.adjudicationNotes = appealNotes;
        this.updatedAt = Instant.now();
    }

    public void markPaid(String eobReference) {
        if (this.status != ClaimStatus.APPROVED && this.status != ClaimStatus.PARTIALLY_APPROVED) {
            throw new IllegalStateException("Claim must be approved to mark as paid");
        }
        this.status = ClaimStatus.PAID;
        this.eobReference = eobReference;
        this.updatedAt = Instant.now();
    }

    public void close() {
        this.status = ClaimStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    public void recordPatientBreakdown(BigDecimal copay, BigDecimal deductible, BigDecimal coinsurance) {
        this.copayAmount = copay;
        this.deductibleAmount = deductible;
        this.coinsuranceAmount = coinsurance;
        this.patientResponsibility = copay.add(deductible).add(coinsurance);
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getClaimNumber() { return claimNumber; }
    public UUID getInvoiceId() { return invoiceId; }
    public UUID getPatientId() { return patientId; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public String getPolicyNumber() { return policyNumber; }
    public String getGroupNumber() { return groupNumber; }
    public String getSubscriberName() { return subscriberName; }
    public String getSubscriberId() { return subscriberId; }
    public BigDecimal getBilledAmount() { return billedAmount; }
    public BigDecimal getAllowedAmount() { return allowedAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getPatientResponsibility() { return patientResponsibility; }
    public BigDecimal getCopayAmount() { return copayAmount; }
    public BigDecimal getDeductibleAmount() { return deductibleAmount; }
    public BigDecimal getCoinsuranceAmount() { return coinsuranceAmount; }
    public ClaimStatus getStatus() { return status; }
    public Instant getSubmittedAt() { return submittedAt; }
    public Instant getProcessedAt() { return processedAt; }
    public LocalDate getServiceDate() { return serviceDate; }
    public String getDenialReason() { return denialReason; }
    public String getDenialCode() { return denialCode; }
    public String getAdjudicationNotes() { return adjudicationNotes; }
    public String getEobReference() { return eobReference; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }

    public static class Builder {
        private String claimNumber;
        private UUID invoiceId;
        private UUID patientId;
        private String insuranceProvider;
        private String policyNumber;
        private String groupNumber;
        private String subscriberName;
        private String subscriberId;
        private BigDecimal billedAmount;
        private LocalDate serviceDate;
        private String createdBy;

        public Builder claimNumber(String claimNumber) {
            this.claimNumber = claimNumber;
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

        public Builder insuranceProvider(String insuranceProvider) {
            this.insuranceProvider = insuranceProvider;
            return this;
        }

        public Builder policyNumber(String policyNumber) {
            this.policyNumber = policyNumber;
            return this;
        }

        public Builder groupNumber(String groupNumber) {
            this.groupNumber = groupNumber;
            return this;
        }

        public Builder subscriberName(String subscriberName) {
            this.subscriberName = subscriberName;
            return this;
        }

        public Builder subscriberId(String subscriberId) {
            this.subscriberId = subscriberId;
            return this;
        }

        public Builder billedAmount(BigDecimal billedAmount) {
            this.billedAmount = billedAmount;
            return this;
        }

        public Builder serviceDate(LocalDate serviceDate) {
            this.serviceDate = serviceDate;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public InsuranceClaim build() {
            if (claimNumber == null || claimNumber.isBlank()) {
                throw new IllegalStateException("Claim number is required");
            }
            if (invoiceId == null) {
                throw new IllegalStateException("Invoice ID is required");
            }
            if (patientId == null) {
                throw new IllegalStateException("Patient ID is required");
            }
            if (insuranceProvider == null || insuranceProvider.isBlank()) {
                throw new IllegalStateException("Insurance provider is required");
            }
            if (policyNumber == null || policyNumber.isBlank()) {
                throw new IllegalStateException("Policy number is required");
            }
            if (billedAmount == null) {
                throw new IllegalStateException("Billed amount is required");
            }
            if (serviceDate == null) {
                serviceDate = LocalDate.now();
            }
            return new InsuranceClaim(this);
        }
    }
}
