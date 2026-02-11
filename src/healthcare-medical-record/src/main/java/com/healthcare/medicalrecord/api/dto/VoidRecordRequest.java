package com.healthcare.medicalrecord.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VoidRecordRequest(

    @NotBlank(message = "Void reason is required")
    @Size(max = 2000, message = "Reason must be at most 2000 characters")
    String reason
) {}
