package com.healthcare.medicalrecord.api.dto;

import com.healthcare.medicalrecord.domain.RecordStatus;
import com.healthcare.medicalrecord.domain.RecordType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordSummaryResponse(
    UUID id,
    String recordNumber,
    UUID patientId,
    UUID providerId,
    RecordType recordType,
    LocalDateTime recordDate,
    String chiefComplaint,
    String primaryDiagnosisCode,
    String primaryDiagnosisDescription,
    RecordStatus status,
    int attachmentsCount,
    Instant createdAt
) {}
