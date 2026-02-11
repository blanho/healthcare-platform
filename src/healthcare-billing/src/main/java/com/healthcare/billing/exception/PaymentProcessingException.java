package com.healthcare.billing.exception;

import com.healthcare.billing.domain.PaymentStatus;

import java.util.UUID;

public class PaymentProcessingException extends RuntimeException {

    private final UUID paymentId;
    private final String referenceNumber;
    private final PaymentStatus status;
    private final String failureCode;

    public PaymentProcessingException(String message) {
        super(message);
        this.paymentId = null;
        this.referenceNumber = null;
        this.status = null;
        this.failureCode = null;
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.paymentId = null;
        this.referenceNumber = null;
        this.status = null;
        this.failureCode = null;
    }

    public PaymentProcessingException(UUID paymentId, String referenceNumber,
                                       PaymentStatus status, String failureCode,
                                       String message) {
        super(message);
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.failureCode = failureCode;
    }

    public UUID getPaymentId() { return paymentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public PaymentStatus getStatus() { return status; }
    public String getFailureCode() { return failureCode; }
}
