package com.healthcare.medicalrecord.api.dto;

import com.healthcare.medicalrecord.domain.RecordType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateMedicalRecordRequest(

    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Provider ID is required")
    UUID providerId,

    UUID appointmentId,

    @NotNull(message = "Record type is required")
    RecordType recordType,

    @NotNull(message = "Record date is required")
    LocalDateTime recordDate,

    @Size(max = 2000, message = "Chief complaint must be at most 2000 characters")
    String chiefComplaint,

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    String notes,

    @Valid
    VitalSignsRequest vitalSigns,

    @Valid
    SoapNoteRequest soapNote,

    @Valid
    List<DiagnosisRequest> diagnoses
) {}
