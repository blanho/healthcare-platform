package com.healthcare.billing.constant;

public final class BillingConstants {

    private BillingConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final String INVOICE_NUMBER_PREFIX = "INV";
    public static final int INVOICE_NUMBER_LENGTH = 10;
    public static final int DEFAULT_PAYMENT_TERMS_DAYS = 30;

    public static final String PAYMENT_REFERENCE_PREFIX = "PAY";
    public static final int PAYMENT_REFERENCE_LENGTH = 12;

    public static final String CLAIM_NUMBER_PREFIX = "CLM";
    public static final int CLAIM_NUMBER_LENGTH = 12;
    public static final int CLAIM_SUBMISSION_TIMEOUT_DAYS = 90;

    public static final String DEFAULT_TAX_RATE = "0.08";
    public static final String MAX_DISCOUNT_PERCENTAGE = "50.00";

    public static final String LATE_PAYMENT_FEE_PERCENTAGE = "5.00";
    public static final int OVERDUE_GRACE_PERIOD_DAYS = 7;
}
