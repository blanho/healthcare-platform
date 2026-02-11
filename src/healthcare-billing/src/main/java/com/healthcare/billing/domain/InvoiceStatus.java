package com.healthcare.billing.domain;

public enum InvoiceStatus {
    DRAFT("Draft invoice, not yet finalized"),
    PENDING("Awaiting payment"),
    PARTIALLY_PAID("Partial payment received"),
    PAID("Fully paid"),
    OVERDUE("Past due date with outstanding balance"),
    CANCELLED("Invoice cancelled"),
    REFUNDED("Invoice refunded"),
    WRITE_OFF("Written off as uncollectable");

    private final String description;

    InvoiceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canAcceptPayment() {
        return this == PENDING || this == PARTIALLY_PAID || this == OVERDUE;
    }

    public boolean canCancel() {
        return this == DRAFT || this == PENDING;
    }

    public boolean canRefund() {
        return this == PAID || this == PARTIALLY_PAID;
    }

    public boolean isFinalized() {
        return this != DRAFT;
    }

    public boolean isClosed() {
        return this == PAID || this == CANCELLED || this == REFUNDED || this == WRITE_OFF;
    }
}
