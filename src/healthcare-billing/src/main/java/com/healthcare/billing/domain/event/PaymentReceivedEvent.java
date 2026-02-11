package com.healthcare.billing.domain.event;

import com.healthcare.billing.domain.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentReceivedEvent extends BillingEvent {

    private final UUID paymentId;
    private final String referenceNumber;
    private final UUID invoiceId;
    private final UUID patientId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final BigDecimal remainingBalance;

    public PaymentReceivedEvent(UUID paymentId, String referenceNumber,
                                 UUID invoiceId, UUID patientId,
                                 BigDecimal amount, PaymentMethod paymentMethod,
                                 BigDecimal remainingBalance, String triggeredBy) {
        super(triggeredBy);
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.invoiceId = invoiceId;
        this.patientId = patientId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.remainingBalance = remainingBalance;
    }

    @Override
    public String getEventType() {
        return "PAYMENT_RECEIVED";
    }

    public UUID getPaymentId() { return paymentId; }
    public String getReferenceNumber() { return referenceNumber; }
    public UUID getInvoiceId() { return invoiceId; }
    public UUID getPatientId() { return patientId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
}
