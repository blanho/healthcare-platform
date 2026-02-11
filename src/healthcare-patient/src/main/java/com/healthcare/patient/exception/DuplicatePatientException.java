package com.healthcare.patient.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class DuplicatePatientException extends BusinessRuleViolationException {

    private DuplicatePatientException(String message) {
        super(message);
    }

    public static DuplicatePatientException byEmail(String email) {
        return new DuplicatePatientException(
            String.format("Patient with email '%s' already exists", email)
        );
    }

    public static DuplicatePatientException byMrn(String mrn) {
        return new DuplicatePatientException(
            String.format("Patient with medical record number '%s' already exists", mrn)
        );
    }

    public static DuplicatePatientException byPhone(String phone) {
        return new DuplicatePatientException(
            String.format("Patient with phone number '%s' already exists", phone)
        );
    }
}
