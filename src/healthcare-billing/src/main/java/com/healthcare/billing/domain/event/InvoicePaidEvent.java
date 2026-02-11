package com.healthcare.billing.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoicePaidEvent extends BillingEvent {

    private final UUID invoiceId;
    private final String invoiceNumber;
    private final UUID patientId;
    private final BigDecimal totalPaid;

    public InvoicePaidEvent(UUID invoiceId, String invoiceNumber,
                            UUID patientId, BigDecimal totalPaid,
                            String triggeredBy) {
        super(triggeredBy);
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.patientId = patientId;
        this.totalPaid = totalPaid;
    }

    @Override
    public String getEventType() {
        return "INVOICE_PAID";
    }

    public UUID getInvoiceId() { return invoiceId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getTotalPaid() { return totalPaid; }
}
