package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.ClaimStatus;
import com.healthcare.billing.domain.InsuranceClaim;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClaimResponse(
    UUID id,
    String claimNumber,
    UUID invoiceId,
    UUID patientId,
    String insuranceProvider,
    String policyNumber,
    String groupNumber,
    String subscriberName,
    String subscriberId,
    BigDecimal billedAmount,
    BigDecimal allowedAmount,
    BigDecimal paidAmount,
    BigDecimal patientResponsibility,
    BigDecimal copayAmount,
    BigDecimal deductibleAmount,
    BigDecimal coinsuranceAmount,
    ClaimStatus status,
    Instant submittedAt,
    Instant processedAt,
    LocalDate serviceDate,
    String denialReason,
    String denialCode,
    String adjudicationNotes,
    String eobReference,
    Instant createdAt,
    Instant updatedAt,
    String createdBy
) {
    public static ClaimResponse from(InsuranceClaim claim) {
        return new ClaimResponse(
            claim.getId(),
            claim.getClaimNumber(),
            claim.getInvoiceId(),
            claim.getPatientId(),
            claim.getInsuranceProvider(),
            claim.getPolicyNumber(),
            claim.getGroupNumber(),
            claim.getSubscriberName(),
            claim.getSubscriberId(),
            claim.getBilledAmount(),
            claim.getAllowedAmount(),
            claim.getPaidAmount(),
            claim.getPatientResponsibility(),
            claim.getCopayAmount(),
            claim.getDeductibleAmount(),
            claim.getCoinsuranceAmount(),
            claim.getStatus(),
            claim.getSubmittedAt(),
            claim.getProcessedAt(),
            claim.getServiceDate(),
            claim.getDenialReason(),
            claim.getDenialCode(),
            claim.getAdjudicationNotes(),
            claim.getEobReference(),
            claim.getCreatedAt(),
            claim.getUpdatedAt(),
            claim.getCreatedBy()
        );
    }
}
