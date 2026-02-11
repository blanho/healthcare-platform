package com.healthcare.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RevenueTrendResponse(
    List<RevenueDataPoint> data,
    String period,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalRevenue,
    BigDecimal averageRevenue
) {
    public record RevenueDataPoint(
        LocalDate date,
        String label,
        BigDecimal revenue,
        BigDecimal collections,
        BigDecimal outstanding
    ) {}
}
