package com.healthcare.medicalrecord.api.dto;

import com.healthcare.medicalrecord.domain.Diagnosis.DiagnosisType;
import com.healthcare.medicalrecord.domain.RecordStatus;
import com.healthcare.medicalrecord.domain.RecordType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MedicalRecordResponse(
    UUID id,
    String recordNumber,
    UUID patientId,
    UUID providerId,
    UUID appointmentId,
    RecordType recordType,
    LocalDateTime recordDate,
    String chiefComplaint,
    String notes,
    VitalSignsResponse vitalSigns,
    SoapNoteResponse soapNote,
    List<DiagnosisResponse> diagnoses,
    RecordStatus status,
    Instant finalizedAt,
    String finalizedBy,
    int attachmentsCount,
    Instant createdAt,
    Instant updatedAt
) {

    public record VitalSignsResponse(
        Integer systolicBp,
        Integer diastolicBp,
        String bloodPressure,
        Integer heartRate,
        Integer respiratoryRate,
        BigDecimal temperature,
        Integer oxygenSaturation,
        BigDecimal weightKg,
        BigDecimal heightCm,
        BigDecimal bmi,
        Integer painLevel,
        Instant recordedAt,
        boolean hasCriticalValue
    ) {}

    public record SoapNoteResponse(
        String subjective,
        String objective,
        String assessment,
        String plan,
        boolean isComplete
    ) {}

    public record DiagnosisResponse(
        String code,
        String description,
        DiagnosisType type,
        boolean primary,
        LocalDate onsetDate,
        LocalDate resolvedDate,
        String notes,
        boolean isResolved,
        boolean isChronic
    ) {}
}
