package com.healthcare.patient.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;
import com.healthcare.patient.domain.PatientStatus;

public class InvalidPatientStatusException extends BusinessRuleViolationException {

    public InvalidPatientStatusException(PatientStatus currentStatus, PatientStatus targetStatus) {
        super(String.format(
            "Cannot transition patient from '%s' to '%s'",
            currentStatus, targetStatus
        ));
    }

    public InvalidPatientStatusException(String message) {
        super(message);
    }

    public static InvalidPatientStatusException cannotActivateDeceased() {
        return new InvalidPatientStatusException("Cannot activate a deceased patient");
    }

    public static InvalidPatientStatusException cannotScheduleInactive() {
        return new InvalidPatientStatusException("Cannot schedule appointments for inactive patient");
    }
}
