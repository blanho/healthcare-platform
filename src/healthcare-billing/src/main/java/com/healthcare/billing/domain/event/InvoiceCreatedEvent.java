package com.healthcare.billing.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceCreatedEvent extends BillingEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final UUID patientId;
    private final BigDecimal totalAmount;

    public InvoiceCreatedEvent(UUID invoiceId, String invoiceNumber,
                               UUID patientId, BigDecimal totalAmount,
                               String triggeredBy) {
        super(triggeredBy);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
    }

    @Override
    public String getEventType() {
        return "INVOICE_CREATED";
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
