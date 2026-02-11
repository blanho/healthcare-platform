package com.healthcare.billing.domain.event;

import com.healthcare.billing.domain.ClaimStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class ClaimSubmittedEvent extends BillingEvent {

    private final UUID claimId;
    private final String claimNumber;
    private final UUID invoiceId;
    private final UUID patientId;
    private final String insuranceProvider;
    private final BigDecimal billedAmount;

    public ClaimSubmittedEvent(UUID claimId, String claimNumber,
                                UUID invoiceId, UUID patientId,
                                String insuranceProvider, BigDecimal billedAmount,
                                String triggeredBy) {
        super(triggeredBy);
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.invoiceId = invoiceId;
        this.patientId = patientId;
        this.insuranceProvider = insuranceProvider;
        this.billedAmount = billedAmount;
    }

    @Override
    public String getEventType() {
        return "CLAIM_SUBMITTED";
    }

    public UUID getClaimId() { return claimId; }
    public String getClaimNumber() { return claimNumber; }
    public UUID getInvoiceId() { return invoiceId; }
    public UUID getPatientId() { return patientId; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public BigDecimal getBilledAmount() { return billedAmount; }
}
