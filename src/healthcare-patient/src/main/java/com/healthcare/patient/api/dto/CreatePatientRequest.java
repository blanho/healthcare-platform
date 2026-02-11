package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.BloodType;
import com.healthcare.patient.domain.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be at most 100 characters")
    String firstName,

    @Size(max = 100, message = "Middle name must be at most 100 characters")
    String middleName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must be at most 100 characters")
    String lastName,

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    @NotNull(message = "Gender is required")
    Gender gender,

    BloodType bloodType,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    String email,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid secondary phone number format")
    String secondaryPhone,

    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "SSN must be in format XXX-XX-XXXX")
    String socialSecurityNumber,

    @Valid
    AddressDto address,

    @Valid
    InsuranceDto insurance,

    @Valid
    EmergencyContactDto emergencyContact
) {}
