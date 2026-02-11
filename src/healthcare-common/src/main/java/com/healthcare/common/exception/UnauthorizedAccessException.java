package com.healthcare.common.exception;

public class UnauthorizedAccessException extends HealthcareException {

    private static final String ERROR_CODE = "UNAUTHORIZED_ACCESS";

    public UnauthorizedAccessException(String message) {
        super(message, ERROR_CODE);
    }
}
