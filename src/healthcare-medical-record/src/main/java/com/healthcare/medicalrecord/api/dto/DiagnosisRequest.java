package com.healthcare.medicalrecord.api.dto;

import com.healthcare.medicalrecord.domain.Diagnosis.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record DiagnosisRequest(

    @NotBlank(message = "Diagnosis code is required")
    @Pattern(regexp = "^[A-Za-z]\\d{2}(\\.\\d{1,4}[A-Za-z]?)?$",
             message = "Invalid ICD-10 code format")
    String code,

    @NotBlank(message = "Diagnosis description is required")
    @Size(max = 500, message = "Description must be at most 500 characters")
    String description,

    DiagnosisType type,

    boolean primary,

    LocalDate onsetDate,

    LocalDate resolvedDate,

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    String notes
) {}
