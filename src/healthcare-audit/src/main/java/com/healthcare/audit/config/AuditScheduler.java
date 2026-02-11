package com.healthcare.audit.config;

import com.healthcare.audit.service.AuditService;
import com.healthcare.audit.service.ComplianceReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Component
@EnableScheduling
public class AuditScheduler {

    private static final Logger log = LoggerFactory.getLogger(AuditScheduler.class);

    private static final long HIGH_ACTIVITY_THRESHOLD = 500;

    private final AuditService auditService;
    private final ComplianceReportService complianceReportService;

    public AuditScheduler(AuditService auditService,
                         ComplianceReportService complianceReportService) {
        this.auditService = auditService;
        this.complianceReportService = complianceReportService;
    }

    @Scheduled(fixedRate = 900000)
    public void checkAnomalousActivity() {
        log.debug("Running anomaly detection check");

        Instant since = Instant.now().minus(1, ChronoUnit.HOURS);
        List<UUID> highActivityUsers = auditService.getUsersWithHighActivity(since, HIGH_ACTIVITY_THRESHOLD);

        if (!highActivityUsers.isEmpty()) {
            log.warn("Detected {} users with high activity in the last hour: {}",
                highActivityUsers.size(), highActivityUsers);

            for (UUID userId : highActivityUsers) {
                if (auditService.hasAnomalousActivity(userId, since)) {
                    log.warn("User {} has anomalous activity patterns", userId);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyReport() {
        log.info("Generating daily audit summary report");

        LocalDate yesterday = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        Instant startTime = yesterday.atStartOfDay(ZoneOffset.UTC).toInstant();

        try {
            complianceReportService.generateDailySummary(startTime);
            log.info("Daily audit summary report generated for {}", yesterday);
        } catch (Exception e) {
            log.error("Failed to generate daily audit report", e);
        }
    }

    @Scheduled(cron = "0 0 1 * * MON")
    public void generateWeeklyReport() {
        log.info("Generating weekly audit summary report");

        Instant weekStart = Instant.now().minus(7, ChronoUnit.DAYS);

        try {
            complianceReportService.generateWeeklySummary(weekStart);
            log.info("Weekly audit summary report generated");
        } catch (Exception e) {
            log.error("Failed to generate weekly audit report", e);
        }
    }

    @Scheduled(cron = "0 0 2 1 * *")
    public void generateMonthlyReport() {
        log.info("Generating monthly audit summary report");

        LocalDate lastMonth = LocalDate.now(ZoneOffset.UTC).minusMonths(1);

        try {
            complianceReportService.generateMonthlySummary(
                lastMonth.getYear(), lastMonth.getMonthValue());
            log.info("Monthly audit summary report generated for {}/{}",
                lastMonth.getMonthValue(), lastMonth.getYear());
        } catch (Exception e) {
            log.error("Failed to generate monthly audit report", e);
        }
    }
}
