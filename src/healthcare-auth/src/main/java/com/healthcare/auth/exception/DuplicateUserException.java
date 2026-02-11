package com.healthcare.auth.exception;

import com.healthcare.common.exception.HealthcareException;

public class DuplicateUserException extends HealthcareException {

    private static final String ERROR_CODE = "DUPLICATE_USER";

    public DuplicateUserException(String message) {
        super(message, ERROR_CODE);
    }

    public static DuplicateUserException usernameExists(String username) {
        return new DuplicateUserException("Username already exists: " + username);
    }

    public static DuplicateUserException emailExists(String email) {
        return new DuplicateUserException("Email already exists: " + email);
    }
}
