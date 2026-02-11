package com.healthcare.dashboard.dto;

import java.time.LocalDate;
import java.util.List;

public record TrendDataResponse(
    List<TrendDataPoint> data,
    String period,
    LocalDate startDate,
    LocalDate endDate
) {
    public record TrendDataPoint(
        LocalDate date,
        String label,
        long value
    ) {}
}
