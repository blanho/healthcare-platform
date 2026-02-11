package com.healthcare.appointment.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class InvalidAppointmentStateException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "INVALID_APPOINTMENT_STATE";

    public InvalidAppointmentStateException(String message) {
        super(message, ERROR_CODE);
    }

    public static InvalidAppointmentStateException cannotTransition(String currentStatus, String targetAction) {
        return new InvalidAppointmentStateException(
            String.format("Cannot %s appointment in status: %s", targetAction, currentStatus)
        );
    }

    public static InvalidAppointmentStateException pastAppointment() {
        return new InvalidAppointmentStateException("Cannot modify past appointments");
    }
}
