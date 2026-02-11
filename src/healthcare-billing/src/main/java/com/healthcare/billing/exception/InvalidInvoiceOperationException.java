package com.healthcare.billing.exception;

import com.healthcare.billing.domain.InvoiceStatus;

import java.util.UUID;

public class InvalidInvoiceOperationException extends RuntimeException {

    private final UUID invoiceId;
    private final InvoiceStatus currentStatus;
    private final String operation;

    public InvalidInvoiceOperationException(UUID invoiceId, InvoiceStatus currentStatus, String operation) {
        super(String.format("Cannot perform operation '%s' on invoice %s in status %s",
                operation, invoiceId, currentStatus));
        this.invoiceId = invoiceId;
        this.currentStatus = currentStatus;
        this.operation = operation;
    }

    public InvalidInvoiceOperationException(String message) {
        super(message);
        this.invoiceId = null;
        this.currentStatus = null;
        this.operation = null;
    }

    public UUID getInvoiceId() { return invoiceId; }
    public InvoiceStatus getCurrentStatus() { return currentStatus; }
    public String getOperation() { return operation; }
}
