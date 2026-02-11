package com.healthcare.common.exception;

import lombok.Getter;

@Getter
public abstract class HealthcareException extends RuntimeException {

    private final String errorCode;

    protected HealthcareException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected HealthcareException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
