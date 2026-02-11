package com.healthcare.dashboard.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
    long totalPatients,
    long activePatients,
    long totalProviders,
    long activeProviders,
    long todayAppointments,
    long pendingAppointments,
    long completedAppointments,
    long cancelledAppointments,
    BigDecimal monthlyRevenue,
    BigDecimal outstandingBalance,
    long overdueInvoices,
    TrendInfo patientsTrend,
    TrendInfo appointmentsTrend,
    TrendInfo revenueTrend
) {
    public record TrendInfo(
        double percentageChange,
        String direction,
        String comparisonPeriod
    ) {}
}
