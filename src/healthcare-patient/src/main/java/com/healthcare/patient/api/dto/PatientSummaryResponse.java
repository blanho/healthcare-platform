package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.PatientStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PatientSummaryResponse(
    UUID id,
    String fullName,
    String email,
    String phoneNumber,
    String medicalRecordNumber,
    LocalDate dateOfBirth,
    int age,
    PatientStatus status
) {}
