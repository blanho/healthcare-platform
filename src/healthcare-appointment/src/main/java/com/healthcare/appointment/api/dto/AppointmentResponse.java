package com.healthcare.appointment.api.dto;

import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.appointment.domain.AppointmentType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentResponse(
    UUID id,
    String appointmentNumber,
    UUID patientId,
    UUID providerId,
    LocalDate scheduledDate,
    LocalTime startTime,
    LocalTime endTime,
    int durationMinutes,
    AppointmentType appointmentType,
    AppointmentStatus status,
    String reasonForVisit,
    String notes,
    CancellationInfo cancellation,
    CheckInInfo checkIn,
    Instant completedAt,
    String completionNotes,
    Instant createdAt,
    Instant updatedAt
) {

    public record CancellationInfo(
        String reason,
        Instant cancelledAt,
        boolean cancelledByPatient
    ) {}

    public record CheckInInfo(
        Instant checkedInAt,
        String notes
    ) {}
}
