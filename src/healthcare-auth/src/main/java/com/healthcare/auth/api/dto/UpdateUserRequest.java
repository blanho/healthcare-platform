package com.healthcare.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Email(message = "Invalid email format")
    String email,

    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName,

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber
) {}
