package com.healthcare.provider.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ProviderNotFoundException extends ResourceNotFoundException {

    private ProviderNotFoundException(String identifier) {
        super("Provider", identifier);
    }

    private ProviderNotFoundException(UUID id) {
        super("Provider", id);
    }

    public static ProviderNotFoundException byId(UUID id) {
        return new ProviderNotFoundException(id);
    }

    public static ProviderNotFoundException byProviderNumber(String providerNumber) {
        return new ProviderNotFoundException("provider number: " + providerNumber);
    }

    public static ProviderNotFoundException byEmail(String email) {
        return new ProviderNotFoundException("email: " + email);
    }

    public static ProviderNotFoundException byLicenseNumber(String licenseNumber) {
        return new ProviderNotFoundException("license number: " + licenseNumber);
    }
}
