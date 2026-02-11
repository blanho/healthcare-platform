package com.healthcare.dashboard.service;

import com.healthcare.dashboard.dto.*;
import com.healthcare.appointment.service.AppointmentService;
import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.billing.service.InvoiceService;
import com.healthcare.billing.domain.InvoiceStatus;
import com.healthcare.patient.service.PatientService;
import com.healthcare.patient.domain.PatientStatus;
import com.healthcare.provider.service.ProviderService;
import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.audit.service.AuditQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientService patientService;
    private final ProviderService providerService;
    private final AppointmentService appointmentService;
    private final InvoiceService invoiceService;
    private final AuditQueryService auditQueryService;

    public DashboardStatsResponse getStats() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);

        long totalPatients = patientService.count();
        long activePatients = patientService.countByStatus(PatientStatus.ACTIVE);
        long lastMonthPatients = patientService.countCreatedBefore(lastMonth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        long totalProviders = providerService.count();
        long activeProviders = providerService.countByStatus(ProviderStatus.ACTIVE);

        long todayAppointments = appointmentService.countByDate(today);
        long pendingAppointments = appointmentService.countByStatus(AppointmentStatus.SCHEDULED);
        long completedAppointments = appointmentService.countByStatusAndDateRange(
            AppointmentStatus.COMPLETED,
            today.withDayOfMonth(1),
            today
        );
        long cancelledAppointments = appointmentService.countByStatusAndDateRange(
            AppointmentStatus.CANCELLED,
            today.withDayOfMonth(1),
            today
        );

        BigDecimal monthlyRevenue = invoiceService.getRevenueForPeriod(
            today.withDayOfMonth(1),
            today
        );
        BigDecimal outstandingBalance = invoiceService.getTotalOutstanding();
        long overdueInvoices = invoiceService.countByStatus(InvoiceStatus.OVERDUE);

        long prevMonthPatients = lastMonthPatients > 0 ? lastMonthPatients : 1;
        double patientGrowth = ((double)(totalPatients - prevMonthPatients) / prevMonthPatients) * 100;

        long lastMonthAppointments = appointmentService.countByDateRange(
            lastMonth.withDayOfMonth(1),
            lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())
        );
        long thisMonthAppointments = appointmentService.countByDateRange(
            today.withDayOfMonth(1),
            today
        );
        double appointmentGrowth = lastMonthAppointments > 0
            ? ((double)(thisMonthAppointments - lastMonthAppointments) / lastMonthAppointments) * 100
            : 0;

        BigDecimal lastMonthRevenue = invoiceService.getRevenueForPeriod(
            lastMonth.withDayOfMonth(1),
            lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())
        );
        double revenueGrowth = lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0
            ? monthlyRevenue.subtract(lastMonthRevenue)
                .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue()
            : 0;

        return new DashboardStatsResponse(
            totalPatients,
            activePatients,
            totalProviders,
            activeProviders,
            todayAppointments,
            pendingAppointments,
            completedAppointments,
            cancelledAppointments,
            monthlyRevenue,
            outstandingBalance,
            overdueInvoices,
            new DashboardStatsResponse.TrendInfo(
                Math.abs(patientGrowth),
                patientGrowth >= 0 ? "UP" : "DOWN",
                "vs last month"
            ),
            new DashboardStatsResponse.TrendInfo(
                Math.abs(appointmentGrowth),
                appointmentGrowth >= 0 ? "UP" : "DOWN",
                "vs last month"
            ),
            new DashboardStatsResponse.TrendInfo(
                Math.abs(revenueGrowth),
                revenueGrowth >= 0 ? "UP" : "DOWN",
                "vs last month"
            )
        );
    }

    public TrendDataResponse getAppointmentTrends(LocalDate startDate, LocalDate endDate, String period) {
        List<TrendDataResponse.TrendDataPoint> dataPoints = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate periodEnd = switch (period.toUpperCase()) {
                case "WEEKLY" -> current.plusWeeks(1).minusDays(1);
                case "MONTHLY" -> current.plusMonths(1).minusDays(1);
                default -> current;
            };

            if (periodEnd.isAfter(endDate)) {
                periodEnd = endDate;
            }

            long count = appointmentService.countByDateRange(current, periodEnd);
            dataPoints.add(new TrendDataResponse.TrendDataPoint(
                current,
                formatDateLabel(current, period),
                count
            ));

            current = switch (period.toUpperCase()) {
                case "WEEKLY" -> current.plusWeeks(1);
                case "MONTHLY" -> current.plusMonths(1);
                default -> current.plusDays(1);
            };
        }

        return new TrendDataResponse(dataPoints, period.toUpperCase(), startDate, endDate);
    }

    public RevenueTrendResponse getRevenueTrends(LocalDate startDate, LocalDate endDate, String period) {
        List<RevenueTrendResponse.RevenueDataPoint> dataPoints = new ArrayList<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        LocalDate current = startDate;
        int periodCount = 0;

        while (!current.isAfter(endDate)) {
            LocalDate periodEnd = switch (period.toUpperCase()) {
                case "WEEKLY" -> current.plusWeeks(1).minusDays(1);
                case "MONTHLY" -> current.plusMonths(1).minusDays(1);
                default -> current;
            };

            if (periodEnd.isAfter(endDate)) {
                periodEnd = endDate;
            }

            BigDecimal revenue = invoiceService.getRevenueForPeriod(current, periodEnd);
            BigDecimal collections = invoiceService.getCollectionsForPeriod(current, periodEnd);
            BigDecimal outstanding = invoiceService.getOutstandingForPeriod(current, periodEnd);

            totalRevenue = totalRevenue.add(revenue);
            periodCount++;

            dataPoints.add(new RevenueTrendResponse.RevenueDataPoint(
                current,
                formatDateLabel(current, period),
                revenue,
                collections,
                outstanding
            ));

            current = switch (period.toUpperCase()) {
                case "WEEKLY" -> current.plusWeeks(1);
                case "MONTHLY" -> current.plusMonths(1);
                default -> current.plusDays(1);
            };
        }

        BigDecimal averageRevenue = periodCount > 0
            ? totalRevenue.divide(BigDecimal.valueOf(periodCount), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return new RevenueTrendResponse(
            dataPoints,
            period.toUpperCase(),
            startDate,
            endDate,
            totalRevenue,
            averageRevenue
        );
    }

    public RecentActivityResponse getRecentActivity(int limit) {
        var recentEvents = auditQueryService.getRecentEvents(limit);

        List<RecentActivityResponse.ActivityItem> activities = recentEvents.stream()
            .map(event -> new RecentActivityResponse.ActivityItem(
                event.id().toString(),
                mapAuditActionToActivityType(event.action().name(), event.resourceCategory().name()),
                formatActivityTitle(event.action().name(), event.resourceCategory().name()),
                event.description(),
                event.eventTimestamp(),
                event.username(),
                event.userRole(),
                event.resourceId() != null ? event.resourceId().toString() : null,
                event.resourceCategory().name()
            ))
            .collect(Collectors.toList());

        return new RecentActivityResponse(activities, activities.size());
    }

    public UpcomingAppointmentsResponse getUpcomingAppointments(int limit) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        var appointments = appointmentService.getUpcoming(limit);

        List<UpcomingAppointmentsResponse.UpcomingAppointment> upcomingList = appointments.stream()
            .map(apt -> new UpcomingAppointmentsResponse.UpcomingAppointment(
                apt.id(),
                apt.appointmentNumber(),
                apt.scheduledDate(),
                apt.startTime(),
                apt.endTime(),
                calculateDuration(apt.startTime(), apt.endTime()),
                apt.appointmentType().name(),
                apt.status().name(),
                new UpcomingAppointmentsResponse.PatientInfo(
                    apt.patientId(),
                    getPatientName(apt.patientId()),
                    getPatientMrn(apt.patientId())
                ),
                new UpcomingAppointmentsResponse.ProviderInfo(
                    apt.providerId(),
                    getProviderName(apt.providerId()),
                    getProviderSpecialty(apt.providerId())
                )
            ))
            .collect(Collectors.toList());

        int todayCount = (int) upcomingList.stream()
            .filter(a -> a.date().equals(today))
            .count();
        int tomorrowCount = (int) upcomingList.stream()
            .filter(a -> a.date().equals(tomorrow))
            .count();

        return new UpcomingAppointmentsResponse(
            upcomingList,
            upcomingList.size(),
            todayCount,
            tomorrowCount
        );
    }

    private String formatDateLabel(LocalDate date, String period) {
        return switch (period.toUpperCase()) {
            case "WEEKLY" -> "Week of " + date.toString();
            case "MONTHLY" -> date.getMonth().toString().substring(0, 3) + " " + date.getYear();
            default -> date.toString();
        };
    }

    private String mapAuditActionToActivityType(String action, String resourceCategory) {
        return switch (action + "_" + resourceCategory) {
            case "CREATE_PATIENT" -> "PATIENT_REGISTERED";
            case "CREATE_APPOINTMENT" -> "APPOINTMENT_SCHEDULED";
            case "UPDATE_APPOINTMENT" -> "APPOINTMENT_UPDATED";
            case "CREATE_INVOICE" -> "INVOICE_CREATED";
            case "UPDATE_INVOICE" -> "INVOICE_PAID";
            default -> action + "_" + resourceCategory;
        };
    }

    private String formatActivityTitle(String action, String resourceCategory) {
        String resourceName = resourceCategory.toLowerCase().replace("_", " ");
        return switch (action) {
            case "CREATE" -> "New " + resourceName + " created";
            case "UPDATE" -> resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1) + " updated";
            case "DELETE" -> resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1) + " deleted";
            case "READ" -> resourceName.substring(0, 1).toUpperCase() + resourceName.substring(1) + " viewed";
            default -> action + " " + resourceName;
        };
    }

    private String getPatientName(java.util.UUID patientId) {
        try {
            var patient = patientService.findById(patientId);
            return patient.map(p -> p.firstName() + " " + p.lastName()).orElse("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getPatientMrn(java.util.UUID patientId) {
        try {
            var patient = patientService.findById(patientId);
            return patient.map(p -> p.medicalRecordNumber()).orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    private String getProviderName(java.util.UUID providerId) {
        try {
            var provider = providerService.findById(providerId);
            return provider.map(p -> "Dr. " + p.firstName() + " " + p.lastName()).orElse("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private String getProviderSpecialty(java.util.UUID providerId) {
        try {
            var provider = providerService.findById(providerId);
            return provider.map(p -> p.specialization()).orElse("");
        } catch (Exception e) {
            return "";
        }
    }

    private int calculateDuration(LocalTime startTime, LocalTime endTime) {
        return (int) ChronoUnit.MINUTES.between(startTime, endTime);
    }
}
