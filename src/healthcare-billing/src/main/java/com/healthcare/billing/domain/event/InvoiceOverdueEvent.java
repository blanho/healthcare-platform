package com.healthcare.billing.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceOverdueEvent extends BillingEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final UUID patientId;
    private final BigDecimal balanceDue;
    private final int daysOverdue;

    public InvoiceOverdueEvent(UUID invoiceId, String invoiceNumber,
                                UUID patientId, BigDecimal balanceDue,
                                int daysOverdue, String triggeredBy) {
        super(triggeredBy);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.patientId = patientId;
        this.balanceDue = balanceDue;
        this.daysOverdue = daysOverdue;
    }

    @Override
    public String getEventType() {
        return "INVOICE_OVERDUE";
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getBalanceDue() { return balanceDue; }
    public int getDaysOverdue() { return daysOverdue; }
}
