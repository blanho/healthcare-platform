package com.healthcare.audit.service;

import com.healthcare.audit.api.dto.ComplianceReportResponse;
import java.time.Instant;

public interface ComplianceReportService {

    ComplianceReportResponse generateHipaaReport(Instant startTime, Instant endTime);

    ComplianceReportResponse generateSecurityReport(Instant startTime, Instant endTime);

    ComplianceReportResponse generatePhiAccessReport(Instant startTime, Instant endTime);

    ComplianceReportResponse generateDailySummary(Instant date);

    ComplianceReportResponse generateWeeklySummary(Instant weekStart);

    ComplianceReportResponse generateMonthlySummary(int year, int month);
}
