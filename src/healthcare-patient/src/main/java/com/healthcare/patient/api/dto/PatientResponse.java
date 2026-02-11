package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.BloodType;
import com.healthcare.patient.domain.Gender;
import com.healthcare.patient.domain.PatientStatus;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(
    UUID id,
    String firstName,
    String middleName,
    String lastName,
    String fullName,
    LocalDate dateOfBirth,
    int age,
    Gender gender,
    BloodType bloodType,
    String email,
    String phoneNumber,
    String secondaryPhone,
    String medicalRecordNumber,
    PatientStatus status,
    boolean isMinor,
    boolean hasActiveInsurance,
    AddressDto address,
    InsuranceDto insurance,
    EmergencyContactDto emergencyContact,
    Instant createdAt,
    Instant updatedAt
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
