package com.healthcare.billing.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {

    private final UUID paymentId;
    private final String referenceNumber;

    public PaymentNotFoundException(UUID paymentId) {
        super("Payment not found with ID: " + paymentId);
        this.paymentId = paymentId;
        this.referenceNumber = null;
    }

    public PaymentNotFoundException(String referenceNumber) {
        super("Payment not found with reference: " + referenceNumber);
        this.paymentId = null;
        this.referenceNumber = referenceNumber;
    }

    public UUID getPaymentId() { return paymentId; }
    public String getReferenceNumber() { return referenceNumber; }
}
