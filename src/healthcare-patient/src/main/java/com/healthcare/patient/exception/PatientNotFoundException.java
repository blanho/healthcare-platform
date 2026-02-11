package com.healthcare.patient.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class PatientNotFoundException extends ResourceNotFoundException {

    public PatientNotFoundException(UUID id) {
        super("Patient", id);
    }

    public PatientNotFoundException(String identifier) {
        super("Patient", identifier);
    }

    public static PatientNotFoundException byId(UUID id) {
        return new PatientNotFoundException(id);
    }

    public static PatientNotFoundException byMrn(String mrn) {
        return new PatientNotFoundException("MRN: " + mrn);
    }

    public static PatientNotFoundException byEmail(String email) {
        return new PatientNotFoundException("email: " + email);
    }
}
