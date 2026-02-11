package com.healthcare.provider.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class InvalidLicenseException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "INVALID_LICENSE";

    public InvalidLicenseException(String message) {
        super(message, ERROR_CODE);
    }

    public static InvalidLicenseException expired(String licenseNumber) {
        return new InvalidLicenseException("License has expired: " + licenseNumber);
    }

    public static InvalidLicenseException invalid(String reason) {
        return new InvalidLicenseException("Invalid license: " + reason);
    }
}
