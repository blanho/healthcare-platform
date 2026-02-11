package com.healthcare.appointment.api.dto;

import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.appointment.domain.AppointmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentSummaryResponse(
    UUID id,
    String appointmentNumber,
    UUID patientId,
    UUID providerId,
    LocalDate scheduledDate,
    LocalTime startTime,
    LocalTime endTime,
    AppointmentType appointmentType,
    AppointmentStatus status
) {}
