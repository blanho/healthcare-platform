package com.healthcare.common.exception;

public class BusinessRuleViolationException extends HealthcareException {

    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessRuleViolationException(String message) {
        super(message, ERROR_CODE);
    }

    public BusinessRuleViolationException(String message, String customErrorCode) {
        super(message, customErrorCode);
    }
}
