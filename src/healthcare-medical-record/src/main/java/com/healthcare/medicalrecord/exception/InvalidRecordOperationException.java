package com.healthcare.medicalrecord.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class InvalidRecordOperationException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "INVALID_RECORD_OPERATION";

    public InvalidRecordOperationException(String message) {
        super(message, ERROR_CODE);
    }

    public static InvalidRecordOperationException cannotEdit(String status) {
        return new InvalidRecordOperationException(
            "Cannot edit medical record in status: " + status
        );
    }

    public static InvalidRecordOperationException cannotFinalize(String status) {
        return new InvalidRecordOperationException(
            "Cannot finalize medical record in status: " + status
        );
    }

    public static InvalidRecordOperationException cannotAmend(String status) {
        return new InvalidRecordOperationException(
            "Cannot amend medical record in status: " + status
        );
    }

    public static InvalidRecordOperationException cannotVoid(String status) {
        return new InvalidRecordOperationException(
            "Cannot void medical record in status: " + status
        );
    }

    public static InvalidRecordOperationException recordLocked() {
        return new InvalidRecordOperationException(
            "Medical record is locked and cannot be modified"
        );
    }
}
