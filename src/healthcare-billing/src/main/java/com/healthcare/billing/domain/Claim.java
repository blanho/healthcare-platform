package com.healthcare.billing.domain;

import com.healthcare.common.domain.AggregateRoot;
import com.healthcare.billing.domain.event.ClaimSubmittedEvent;
import com.healthcare.billing.domain.event.ClaimStatusChangedEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "claims", indexes = {
    @Index(name = "idx_claim_number", columnList = "claim_number", unique = true),
    @Index(name = "idx_claim_patient", columnList = "patient_id"),
    @Index(name = "idx_claim_provider", columnList = "provider_id"),
    @Index(name = "idx_claim_invoice", columnList = "invoice_id"),
    @Index(name = "idx_claim_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Claim extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "claim_number", unique = true, nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "provider_id")
    private UUID providerId;

    @Column(name = "insurance_provider", nullable = false, length = 100)
    private String insuranceName;

    @Column(name = "policy_number", nullable = false, length = 50)
    private String policyNumber;

    @Column(name = "group_number", length = 50)
    private String groupNumber;

    @Column(name = "subscriber_name", length = 100)
    private String subscriberName;

    @Column(name = "subscriber_id", length = 50)
    private String subscriberId;

    @Column(name = "billed_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal billedAmount;

    @Column(name = "allowed_amount", precision = 10, scale = 2)
    private BigDecimal allowedAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "patient_responsibility", precision = 10, scale = 2)
    private BigDecimal patientResponsibility;

    @Column(name = "copay_amount", precision = 10, scale = 2)
    private BigDecimal copayAmount;

    @Column(name = "deductible_amount", precision = 10, scale = 2)
    private BigDecimal deductibleAmount;

    @Column(name = "coinsurance_amount", precision = 10, scale = 2)
    private BigDecimal coinsuranceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ClaimStatus status;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "service_date")
    private LocalDate serviceDate;

    @Column(name = "denial_reason", length = 500)
    private String denialReason;

    @Column(name = "denial_code", length = 20)
    private String denialCode;

    @Column(name = "adjudication_notes", length = 1000)
    private String adjudicationNotes;

    @Column(name = "eob_reference", length = 100)
    private String eobReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    public void submit(String createdBy) {
        Objects.requireNonNull(createdBy, "Created by is required");

        if (this.status != ClaimStatus.DRAFT) {
            throw new IllegalStateException("Only draft claims can be submitted");
        }

        this.status = ClaimStatus.SUBMITTED;
        this.submittedAt = Instant.now();
        this.updatedAt = Instant.now();

        registerEvent(new ClaimSubmittedEvent(
            this.id,
            this.claimNumber,
            this.invoiceId,
            this.patientId,
            this.insuranceName,
            this.billedAmount,
            createdBy
        ));
    }

    public void updateStatus(ClaimStatus newStatus, BigDecimal paidAmount,
                           BigDecimal patientResponsibility, String notes, String updatedBy) {
        Objects.requireNonNull(newStatus, "Status is required");

        ClaimStatus previousStatus = this.status;
        this.status = newStatus;
        this.updatedAt = Instant.now();

        if (newStatus == ClaimStatus.PAID || newStatus == ClaimStatus.DENIED) {
            this.processedAt = Instant.now();
        }

        if (notes != null && !notes.isEmpty()) {
            this.adjudicationNotes = notes;
        }

        registerEvent(new ClaimStatusChangedEvent(
            this.id,
            this.claimNumber,
            previousStatus,
            newStatus,
            paidAmount,
            patientResponsibility,
            notes,
            updatedBy
        ));
    }

    public static Claim createDraft(
            String claimNumber,
            UUID invoiceId,
            UUID patientId,
            UUID providerId,
            String insuranceName,
            String policyNumber,
            BigDecimal billedAmount,
            LocalDate serviceDate,
            String createdBy) {

        Objects.requireNonNull(claimNumber, "Claim number is required");
        Objects.requireNonNull(invoiceId, "Invoice ID is required");
        Objects.requireNonNull(patientId, "Patient ID is required");
        Objects.requireNonNull(insuranceName, "Insurance name is required");
        Objects.requireNonNull(policyNumber, "Policy number is required");
        Objects.requireNonNull(billedAmount, "Billed amount is required");
        Objects.requireNonNull(createdBy, "Created by is required");

        Claim claim = new Claim();
        claim.id = UUID.randomUUID();
        claim.claimNumber = claimNumber;
        claim.invoiceId = invoiceId;
        claim.patientId = patientId;
        claim.providerId = providerId;
        claim.insuranceName = insuranceName;
        claim.policyNumber = policyNumber;
        claim.billedAmount = billedAmount;
        claim.serviceDate = serviceDate;
        claim.status = ClaimStatus.DRAFT;
        claim.createdAt = Instant.now();
        claim.updatedAt = Instant.now();
        claim.createdBy = createdBy;
        claim.paidAmount = BigDecimal.ZERO;

        return claim;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
