package com.healthcare.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record MfaVerifyRequest(
    @NotBlank(message = "Code is required")
    String code,

    boolean useBackupCode
) {}
