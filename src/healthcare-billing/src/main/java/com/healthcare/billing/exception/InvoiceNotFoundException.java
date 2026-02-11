package com.healthcare.billing.exception;

import java.util.UUID;

public class InvoiceNotFoundException extends RuntimeException {

    private final UUID invoiceId;
    private final String invoiceNumber;

    public InvoiceNotFoundException(UUID invoiceId) {
        super("Invoice not found with ID: " + invoiceId);
        this.invoiceId = invoiceId;
        this.invoiceNumber = null;
    }

    public InvoiceNotFoundException(String invoiceNumber) {
        super("Invoice not found with number: " + invoiceNumber);
        this.invoiceId = null;
        this.invoiceNumber = invoiceNumber;
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
}
