package com.healthcare.auth.exception;

import com.healthcare.common.exception.HealthcareException;

public class AccessDeniedException extends HealthcareException {

    private static final String ERROR_CODE = "ACCESS_DENIED";

    public AccessDeniedException(String message) {
        super(message, ERROR_CODE);
    }

    public static AccessDeniedException insufficientPermissions() {
        return new AccessDeniedException("Insufficient permissions to access this resource");
    }

    public static AccessDeniedException forResource(String resource) {
        return new AccessDeniedException("Access denied to resource: " + resource);
    }

    public static AccessDeniedException forAction(String resource, String action) {
        return new AccessDeniedException("Access denied: cannot " + action + " " + resource);
    }
}
