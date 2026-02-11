package com.healthcare.auth.exception;

import com.healthcare.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(UUID id) {
        super("User", id);
    }

    public UserNotFoundException(String identifier) {
        super("User", identifier);
    }

    public static UserNotFoundException byId(UUID id) {
        return new UserNotFoundException(id);
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException("username: " + username);
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("email: " + email);
    }
}
