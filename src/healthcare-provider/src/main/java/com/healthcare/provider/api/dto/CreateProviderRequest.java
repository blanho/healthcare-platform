package com.healthcare.provider.api.dto;

import com.healthcare.provider.domain.ProviderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateProviderRequest(

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    String firstName,

    @Size(max = 100, message = "Middle name must be at most 100 characters")
    String middleName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @NotNull(message = "Provider type is required")
    ProviderType providerType,

    @Size(max = 100, message = "Specialization must be at most 100 characters")
    String specialization,

    @NotNull(message = "License information is required")
    @Valid
    LicenseRequest license,

    @Pattern(regexp = "^\\d{10}$", message = "NPI must be exactly 10 digits")
    String npiNumber,

    String qualification,

    @Min(value = 0, message = "Years of experience must be non-negative")
    Integer yearsOfExperience,

    @DecimalMin(value = "0.00", message = "Consultation fee must be non-negative")
    BigDecimal consultationFee,

    Boolean acceptingPatients
) {

    public record LicenseRequest(
        @NotBlank(message = "License number is required")
        @Size(max = 100, message = "License number must be at most 100 characters")
        String licenseNumber,

        @NotBlank(message = "License state is required")
        @Size(max = 50, message = "License state must be at most 50 characters")
        String licenseState,

        @NotNull(message = "License expiry date is required")
        LocalDate expiryDate
    ) {}
}
