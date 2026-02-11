package com.healthcare.appointment.api.dto;

import com.healthcare.appointment.domain.AppointmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ScheduleAppointmentRequest(

    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Provider ID is required")
    UUID providerId,

    @NotNull(message = "Scheduled date is required")
    @Future(message = "Scheduled date must be in the future")
    LocalDate scheduledDate,

    @NotNull(message = "Start time is required")
    LocalTime startTime,

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    Integer durationMinutes,

    @NotNull(message = "Appointment type is required")
    AppointmentType appointmentType,

    @Size(max = 1000, message = "Reason for visit must be at most 1000 characters")
    String reasonForVisit,

    @Size(max = 2000, message = "Notes must be at most 2000 characters")
    String notes
) {}
