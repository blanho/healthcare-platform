package com.healthcare.medicalrecord.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class DuplicateRecordException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "DUPLICATE_MEDICAL_RECORD";

    public DuplicateRecordException(String message) {
        super(message, ERROR_CODE);
    }

    public static DuplicateRecordException withRecordNumber(String recordNumber) {
        return new DuplicateRecordException(
            "Medical record with number already exists: " + recordNumber
        );
    }
}
