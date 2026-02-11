package com.healthcare.provider.exception;

import com.healthcare.common.exception.BusinessRuleViolationException;

public class DuplicateProviderException extends BusinessRuleViolationException {

    private static final String ERROR_CODE = "DUPLICATE_PROVIDER";

    private DuplicateProviderException(String message) {
        super(message, ERROR_CODE);
    }

    public static DuplicateProviderException byEmail(String email) {
        return new DuplicateProviderException("Provider already exists with email: " + email);
    }

    public static DuplicateProviderException byProviderNumber(String providerNumber) {
        return new DuplicateProviderException("Provider already exists with provider number: " + providerNumber);
    }

    public static DuplicateProviderException byLicenseNumber(String licenseNumber) {
        return new DuplicateProviderException("Provider already exists with license number: " + licenseNumber);
    }
}
