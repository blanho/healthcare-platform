package com.healthcare.appointment.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record RescheduleAppointmentRequest(

    @NotNull(message = "New date is required")
    @Future(message = "New date must be in the future")
    LocalDate newDate,

    @NotNull(message = "New start time is required")
    LocalTime newStartTime,

    @Min(value = 5, message = "Duration must be at least 5 minutes")
    Integer durationMinutes
) {}
