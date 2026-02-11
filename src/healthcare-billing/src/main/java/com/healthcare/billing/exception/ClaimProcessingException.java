package com.healthcare.billing.exception;

import com.healthcare.billing.domain.ClaimStatus;

import java.util.UUID;

public class ClaimProcessingException extends RuntimeException {

    private final UUID claimId;
    private final String claimNumber;
    private final ClaimStatus status;

    public ClaimProcessingException(String message) {
        super(message);
        this.claimId = null;
        this.claimNumber = null;
        this.status = null;
    }

    public ClaimProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.claimId = null;
        this.claimNumber = null;
        this.status = null;
    }

    public ClaimProcessingException(UUID claimId, String claimNumber,
                                     ClaimStatus status, String message) {
        super(message);
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.status = status;
    }

    public UUID getClaimId() { return claimId; }
    public String getClaimNumber() { return claimNumber; }
    public ClaimStatus getStatus() { return status; }
}
