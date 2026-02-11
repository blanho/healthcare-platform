package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.BloodType;
import com.healthcare.patient.domain.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePatientRequest(

    @Size(max = 100, message = "First name must be at most 100 characters")
    String firstName,

    @Size(max = 100, message = "Middle name must be at most 100 characters")
    String middleName,

    @Size(max = 100, message = "Last name must be at most 100 characters")
    String lastName,

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    Gender gender,

    BloodType bloodType,

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid secondary phone number format")
    String secondaryPhone,

    @Valid
    AddressDto address,

    @Valid
    InsuranceDto insurance,

    @Valid
    EmergencyContactDto emergencyContact
) {}
