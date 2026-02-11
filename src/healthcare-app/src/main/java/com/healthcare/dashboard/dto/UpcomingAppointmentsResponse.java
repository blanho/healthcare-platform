package com.healthcare.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record UpcomingAppointmentsResponse(
    List<UpcomingAppointment> appointments,
    int totalCount,
    int todayCount,
    int tomorrowCount
) {
    public record UpcomingAppointment(
        UUID id,
        String appointmentNumber,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int durationMinutes,
        String type,
        String status,
        PatientInfo patient,
        ProviderInfo provider
    ) {}

    public record PatientInfo(
        UUID id,
        String name,
        String mrn
    ) {}

    public record ProviderInfo(
        UUID id,
        String name,
        String specialty
    ) {}
}
