package com.healthcare.billing.domain.event;

import com.healthcare.billing.domain.ClaimStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class ClaimStatusChangedEvent extends BillingEvent {

    private final UUID claimId;
    private final String claimNumber;
    private final ClaimStatus previousStatus;
    private final ClaimStatus newStatus;
    private final BigDecimal paidAmount;
    private final BigDecimal patientResponsibility;
    private final String notes;

    public ClaimStatusChangedEvent(UUID claimId, String claimNumber,
                                    ClaimStatus previousStatus, ClaimStatus newStatus,
                                    BigDecimal paidAmount, BigDecimal patientResponsibility,
                                    String notes, String triggeredBy) {
        super(triggeredBy);
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.paidAmount = paidAmount;
        this.patientResponsibility = patientResponsibility;
        this.notes = notes;
    }

    @Override
    public String getEventType() {
        return "CLAIM_STATUS_CHANGED";
    }

    public UUID getClaimId() { return claimId; }
    public String getClaimNumber() { return claimNumber; }
    public ClaimStatus getPreviousStatus() { return previousStatus; }
    public ClaimStatus getNewStatus() { return newStatus; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getPatientResponsibility() { return patientResponsibility; }
    public String getNotes() { return notes; }
}
