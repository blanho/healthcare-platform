package com.healthcare.billing.domain;

public enum PaymentMethod {
    CASH("Cash payment"),
    CREDIT_CARD("Credit card payment"),
    DEBIT_CARD("Debit card payment"),
    CHECK("Check payment"),
    BANK_TRANSFER("Bank/wire transfer"),
    INSURANCE("Insurance payment"),
    HSA("Health Savings Account"),
    FSA("Flexible Spending Account"),
    PAYMENT_PLAN("Payment plan installment"),
    WRITE_OFF("Written off");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCardPayment() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }

    public boolean isInsuranceRelated() {
        return this == INSURANCE || this == HSA || this == FSA;
    }
}
