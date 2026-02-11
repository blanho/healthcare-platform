package com.healthcare.common.exception;

import java.util.UUID;

public class ResourceNotFoundException extends HealthcareException {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, UUID resourceId) {
        super(
            String.format("%s not found with id: %s", resourceName, resourceId),
            ERROR_CODE
        );
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(
            String.format("%s not found: %s", resourceName, identifier),
            ERROR_CODE
        );
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(
            String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
            ERROR_CODE
        );
    }
}
