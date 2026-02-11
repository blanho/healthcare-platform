package com.healthcare.billing.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record BillingStatisticsResponse(
    BigDecimal totalRevenue,
    BigDecimal outstandingBalance,
    long totalInvoices,
    long paidInvoices,
    long overdueInvoices,
    long pendingClaims,
    long deniedClaims,
    BigDecimal insurancePayments,
    BigDecimal patientPayments,
    Map<String, PaymentMethodStats> paymentMethodBreakdown,
    Map<String, InsuranceProviderStats> insuranceProviderBreakdown
) {
    public record PaymentMethodStats(
        long count,
        BigDecimal totalAmount
    ) {}

    public record InsuranceProviderStats(
        long claimCount,
        BigDecimal billedAmount,
        BigDecimal paidAmount,
        BigDecimal denialRate
    ) {}
}
