package com.healthcare.dashboard.controller;

import com.healthcare.dashboard.dto.*;
import com.healthcare.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard data aggregation endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get dashboard statistics", description = "Returns aggregated statistics for the dashboard with trend data")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }

    @GetMapping("/trends/appointments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get appointment trends", description = "Returns appointment count trends for the specified period")
    public ResponseEntity<TrendDataResponse> getAppointmentTrends(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") String period
    ) {

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return ResponseEntity.ok(dashboardService.getAppointmentTrends(startDate, endDate, period));
    }

    @GetMapping("/trends/revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF', 'MANAGER')")
    @Operation(summary = "Get revenue trends", description = "Returns revenue trends for the specified period")
    public ResponseEntity<RevenueTrendResponse> getRevenueTrends(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAILY") String period
    ) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return ResponseEntity.ok(dashboardService.getRevenueTrends(startDate, endDate, period));
    }

    @GetMapping("/activity")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get recent activity", description = "Returns recent system activity for the activity feed")
    public ResponseEntity<RecentActivityResponse> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getRecentActivity(Math.min(limit, 100)));
    }

    @GetMapping("/appointments/upcoming")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get upcoming appointments", description = "Returns upcoming appointments for the dashboard")
    public ResponseEntity<UpcomingAppointmentsResponse> getUpcomingAppointments(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(dashboardService.getUpcomingAppointments(Math.min(limit, 50)));
    }
}
