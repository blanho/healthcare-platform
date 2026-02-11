package com.healthcare.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaSetupRequest(
    @NotBlank(message = "Secret is required")
    String secret,

    @NotBlank(message = "Code is required")
    String code
) {}
