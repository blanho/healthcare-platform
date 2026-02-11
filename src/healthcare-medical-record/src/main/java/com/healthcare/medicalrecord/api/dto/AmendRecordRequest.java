package com.healthcare.medicalrecord.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AmendRecordRequest(

    @NotBlank(message = "Amendment reason is required")
    @Size(max = 2000, message = "Reason must be at most 2000 characters")
    String reason,

    @Size(max = 5000, message = "Additional notes must be at most 5000 characters")
    String additionalNotes
) {}
