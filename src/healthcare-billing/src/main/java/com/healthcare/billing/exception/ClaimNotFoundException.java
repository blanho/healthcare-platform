package com.healthcare.billing.exception;

import java.util.UUID;

public class ClaimNotFoundException extends RuntimeException {

    private final UUID claimId;
    private final String claimNumber;

    public ClaimNotFoundException(UUID claimId) {
        super("Insurance claim not found with ID: " + claimId);
        this.claimId = claimId;
        this.claimNumber = null;
    }

    public ClaimNotFoundException(String claimNumber) {
        super("Insurance claim not found with number: " + claimNumber);
        this.claimId = null;
        this.claimNumber = claimNumber;
    }

    public UUID getClaimId() { return claimId; }
    public String getClaimNumber() { return claimNumber; }
}
