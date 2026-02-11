package com.healthcare.appointment.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class TimeSlotConflictException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "TIME_SLOT_CONFLICT";

    public TimeSlotConflictException(String message) {
        super(message, ERROR_CODE);
    }

    public static TimeSlotConflictException forProvider(UUID providerId, LocalDate date, LocalTime startTime) {
        return new TimeSlotConflictException(
            String.format("Provider %s already has an appointment on %s at %s", providerId, date, startTime)
        );
    }

    public static TimeSlotConflictException forPatient(UUID patientId, LocalDate date, LocalTime startTime) {
        return new TimeSlotConflictException(
            String.format("Patient %s already has an appointment on %s at %s", patientId, date, startTime)
        );
    }

    public static TimeSlotConflictException slotNotAvailable(LocalDate date, LocalTime startTime) {
        return new TimeSlotConflictException(
            String.format("Time slot on %s at %s is not available", date, startTime)
        );
    }
}
