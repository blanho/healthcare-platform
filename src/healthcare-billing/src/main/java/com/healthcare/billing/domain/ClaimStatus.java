package com.healthcare.billing.domain;

public enum ClaimStatus {
    DRAFT("Claim being prepared"),
    SUBMITTED("Claim submitted to insurer"),
    ACKNOWLEDGED("Claim acknowledged by insurer"),
    IN_REVIEW("Claim under review"),
    PENDING_INFO("Additional information requested"),
    APPROVED("Claim approved"),
    PARTIALLY_APPROVED("Claim partially approved"),
    DENIED("Claim denied"),
    APPEALED("Claim denial appealed"),
    PAID("Claim paid by insurer"),
    CLOSED("Claim closed");

    private final String description;

    ClaimStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canSubmit() {
        return this == DRAFT;
    }

    public boolean canAppeal() {
        return this == DENIED || this == PARTIALLY_APPROVED;
    }

    public boolean isPending() {
        return this == SUBMITTED || this == ACKNOWLEDGED || this == IN_REVIEW || this == APPEALED;
    }

    public boolean isFinal() {
        return this == PAID || this == CLOSED || this == DENIED;
    }
}
