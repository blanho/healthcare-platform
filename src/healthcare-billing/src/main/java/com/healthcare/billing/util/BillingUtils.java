package com.healthcare.billing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class BillingUtils {

    private BillingUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static BigDecimal calculateTax(BigDecimal amount, BigDecimal taxRate) {
        if (amount == null || taxRate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateDiscount(BigDecimal amount, BigDecimal discountPercentage) {
        if (amount == null || discountPercentage == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(discountPercentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "$0.00";
        }
        return String.format("$%,.2f", amount);
    }

    public static boolean isOverdue(java.time.LocalDate dueDate) {
        return dueDate != null && dueDate.isBefore(java.time.LocalDate.now());
    }
}
