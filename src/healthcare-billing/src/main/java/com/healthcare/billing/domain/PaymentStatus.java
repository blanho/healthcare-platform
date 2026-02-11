package com.healthcare.billing.domain;

public enum PaymentStatus {
    PENDING("Payment initiated, awaiting processing"),
    PROCESSING("Payment being processed"),
    COMPLETED("Payment successfully completed"),
    FAILED("Payment failed"),
    CANCELLED("Payment cancelled"),
    REFUNDED("Payment refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }

    public boolean canRefund() {
        return this == COMPLETED;
    }
}
