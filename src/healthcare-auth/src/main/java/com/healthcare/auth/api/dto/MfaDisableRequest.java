package com.healthcare.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaDisableRequest(
    @NotBlank(message = "Password is required for security verification")
    String password
) {}
