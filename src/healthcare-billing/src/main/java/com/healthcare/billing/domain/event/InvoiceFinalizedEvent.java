package com.healthcare.billing.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceFinalizedEvent extends BillingEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final UUID patientId;
    private final BigDecimal totalAmount;
    private final BigDecimal balanceDue;

    public InvoiceFinalizedEvent(UUID invoiceId, String invoiceNumber,
                                  UUID patientId, BigDecimal totalAmount,
                                  BigDecimal balanceDue, String triggeredBy) {
        super(triggeredBy);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.patientId = patientId;
        this.totalAmount = totalAmount;
        this.balanceDue = balanceDue;
    }

    @Override
    public String getEventType() {
        return "INVOICE_FINALIZED";
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getBalanceDue() { return balanceDue; }
}
