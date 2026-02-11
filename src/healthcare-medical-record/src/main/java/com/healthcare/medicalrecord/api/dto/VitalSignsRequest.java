package com.healthcare.medicalrecord.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record VitalSignsRequest(

    @Min(value = 40, message = "Systolic BP must be at least 40")
    @Max(value = 300, message = "Systolic BP must be at most 300")
    Integer systolicBp,

    @Min(value = 20, message = "Diastolic BP must be at least 20")
    @Max(value = 200, message = "Diastolic BP must be at most 200")
    Integer diastolicBp,

    @Min(value = 20, message = "Heart rate must be at least 20")
    @Max(value = 300, message = "Heart rate must be at most 300")
    Integer heartRate,

    @Min(value = 4, message = "Respiratory rate must be at least 4")
    @Max(value = 60, message = "Respiratory rate must be at most 60")
    Integer respiratoryRate,

    @Min(value = 30, message = "Temperature must be at least 30°C")
    @Max(value = 45, message = "Temperature must be at most 45°C")
    BigDecimal temperature,

    @Min(value = 50, message = "Oxygen saturation must be at least 50%")
    @Max(value = 100, message = "Oxygen saturation must be at most 100%")
    Integer oxygenSaturation,

    @Positive(message = "Weight must be positive")
    BigDecimal weightKg,

    @Positive(message = "Height must be positive")
    BigDecimal heightCm,

    @Min(value = 0, message = "Pain level must be at least 0")
    @Max(value = 10, message = "Pain level must be at most 10")
    Integer painLevel,

    Instant recordedAt
) {}
