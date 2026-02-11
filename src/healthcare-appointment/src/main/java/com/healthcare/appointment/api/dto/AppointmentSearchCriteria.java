package com.healthcare.appointment.api.dto;

import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.appointment.domain.AppointmentType;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentSearchCriteria(
    UUID patientId,
    UUID providerId,
    LocalDate startDate,
    LocalDate endDate,
    AppointmentType appointmentType,
    AppointmentStatus status
) {}
