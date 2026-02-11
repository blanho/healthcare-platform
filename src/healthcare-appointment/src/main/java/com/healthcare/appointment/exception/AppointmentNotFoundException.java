package com.healthcare.appointment.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class AppointmentNotFoundException extends ResourceNotFoundException {

    private AppointmentNotFoundException(String identifier) {
        super("Appointment", identifier);
    }

    private AppointmentNotFoundException(UUID id) {
        super("Appointment", id);
    }

    public static AppointmentNotFoundException byId(UUID id) {
        return new AppointmentNotFoundException(id);
    }

    public static AppointmentNotFoundException byAppointmentNumber(String appointmentNumber) {
        return new AppointmentNotFoundException("appointment number: " + appointmentNumber);
    }
}
