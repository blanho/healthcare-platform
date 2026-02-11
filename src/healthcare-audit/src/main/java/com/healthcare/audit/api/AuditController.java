package com.healthcare.audit.api;

import com.healthcare.audit.api.dto.*;
import com.healthcare.audit.domain.*;
import com.healthcare.audit.exception.AuditEventNotFoundException;
import com.healthcare.audit.service.AuditService;
import com.healthcare.audit.service.ComplianceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@Tag(name = "Audit", description = "HIPAA-compliant audit log management")
public class AuditController {

    private final AuditService auditService;
    private final ComplianceReportService complianceReportService;

    public AuditController(AuditService auditService,
                          ComplianceReportService complianceReportService) {
        this.auditService = auditService;
        this.complianceReportService = complianceReportService;
    }

    @GetMapping("/events/{id}")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get audit event by ID")
    public ResponseEntity<AuditEventResponse> getEvent(@PathVariable UUID id) {
        AuditEventResponse event = auditService.getEvent(id)
            .orElseThrow(() -> new AuditEventNotFoundException(id));
        return ResponseEntity.ok(event);
    }

    @GetMapping("/events")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Search audit events")
    public ResponseEntity<Page<AuditEventSummary>> searchEvents(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) ResourceCategory resourceCategory,
            @RequestParam(required = false) AuditAction action,
            @RequestParam(required = false) AuditOutcome outcome,
            @RequestParam(required = false) AuditSeverity severity,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @PageableDefault(size = 20) Pageable pageable) {

        AuditSearchCriteria criteria = AuditSearchCriteria.builder()
            .userId(userId)
            .patientId(patientId)
            .resourceCategory(resourceCategory)
            .action(action)
            .outcome(outcome)
            .severity(severity)
            .startTime(startTime)
            .endTime(endTime)
            .build();

        return ResponseEntity.ok(auditService.searchEvents(criteria, pageable));
    }

    @GetMapping("/events/correlation/{correlationId}")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get audit events by correlation ID")
    public ResponseEntity<List<AuditEventResponse>> getByCorrelationId(
            @PathVariable String correlationId) {
        return ResponseEntity.ok(auditService.getByCorrelationId(correlationId));
    }

    @GetMapping("/users/{userId}/trail")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get user audit trail")
    public ResponseEntity<Page<AuditEventSummary>> getUserAuditTrail(
            @PathVariable UUID userId,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditService.getUserAuditTrail(userId, startTime, endTime, pageable));
    }

    @GetMapping("/users/{userId}/summary")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get user activity summary")
    public ResponseEntity<UserActivitySummary> getUserActivitySummary(
            @PathVariable UUID userId,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime) {
        return ResponseEntity.ok(auditService.getUserActivitySummary(userId, startTime, endTime));
    }

    @GetMapping("/patients/{patientId}/access-history")
    @PreAuthorize("hasAuthority('audit:read') or hasAuthority('patient:read')")
    @Operation(summary = "Get patient access history (HIPAA disclosure tracking)")
    public ResponseEntity<PatientAccessHistory> getPatientAccessHistory(
            @PathVariable UUID patientId,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime) {
        return ResponseEntity.ok(auditService.getPatientAccessHistory(patientId, startTime, endTime));
    }

    @GetMapping("/patients/{patientId}/trail")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get patient audit trail")
    public ResponseEntity<Page<AuditEventSummary>> getPatientAuditTrail(
            @PathVariable UUID patientId,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditService.getPatientAuditTrail(patientId, startTime, endTime, pageable));
    }

    @GetMapping("/resources/{category}/{resourceId}/trail")
    @PreAuthorize("hasAuthority('audit:read')")
    @Operation(summary = "Get resource audit trail")
    public ResponseEntity<Page<AuditEventSummary>> getResourceAuditTrail(
            @PathVariable ResourceCategory category,
            @PathVariable UUID resourceId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditService.getResourceAuditTrail(category, resourceId, pageable));
    }

    @GetMapping("/security-events")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Get security events")
    public ResponseEntity<Page<AuditEventSummary>> getSecurityEvents(
            @RequestParam(required = false) Instant since,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditService.getSecurityEvents(since, pageable));
    }

    @GetMapping("/users/{userId}/anomaly-check")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Check if user has anomalous activity")
    public ResponseEntity<Boolean> checkUserAnomaly(
            @PathVariable UUID userId,
            @RequestParam(required = false) Instant since) {
        Instant effectiveSince = since != null ? since : Instant.now().minusSeconds(3600);
        return ResponseEntity.ok(auditService.hasAnomalousActivity(userId, effectiveSince));
    }

    @GetMapping("/users/{userId}/phi-access-count")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Count user's PHI access events")
    public ResponseEntity<Long> countUserPhiAccess(
            @PathVariable UUID userId,
            @RequestParam(required = false) Instant since) {
        Instant effectiveSince = since != null ? since : Instant.now().minusSeconds(3600);
        return ResponseEntity.ok(auditService.countUserPhiAccess(userId, effectiveSince));
    }

    @GetMapping("/users/{userId}/failed-logins")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Count user's failed login attempts")
    public ResponseEntity<Long> countFailedLogins(
            @PathVariable UUID userId,
            @RequestParam(required = false) Instant since) {
        Instant effectiveSince = since != null ? since : Instant.now().minusSeconds(3600);
        return ResponseEntity.ok(auditService.countFailedLogins(userId, effectiveSince));
    }

    @GetMapping("/reports/hipaa")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Generate HIPAA compliance report")
    public ResponseEntity<ComplianceReportResponse> generateHipaaReport(
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        return ResponseEntity.ok(complianceReportService.generateHipaaReport(startTime, endTime));
    }

    @GetMapping("/reports/security")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Generate security audit report")
    public ResponseEntity<ComplianceReportResponse> generateSecurityReport(
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        return ResponseEntity.ok(complianceReportService.generateSecurityReport(startTime, endTime));
    }

    @GetMapping("/reports/phi-access")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Generate PHI access report")
    public ResponseEntity<ComplianceReportResponse> generatePhiAccessReport(
            @RequestParam Instant startTime,
            @RequestParam Instant endTime) {
        return ResponseEntity.ok(complianceReportService.generatePhiAccessReport(startTime, endTime));
    }

    @GetMapping("/reports/daily")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Generate daily summary report")
    public ResponseEntity<ComplianceReportResponse> generateDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Instant dateInstant = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        return ResponseEntity.ok(complianceReportService.generateDailySummary(dateInstant));
    }

    @GetMapping("/reports/monthly")
    @PreAuthorize("hasAuthority('audit:admin')")
    @Operation(summary = "Generate monthly summary report")
    public ResponseEntity<ComplianceReportResponse> generateMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(complianceReportService.generateMonthlySummary(year, month));
    }
}
