package com.healthcare.medicalrecord.api.dto;

import com.healthcare.medicalrecord.domain.RecordStatus;
import com.healthcare.medicalrecord.domain.RecordType;

import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordSearchCriteria(
    UUID patientId,
    UUID providerId,
    UUID appointmentId,
    RecordType recordType,
    RecordStatus status,
    LocalDateTime startDate,
    LocalDateTime endDate,
    String diagnosisCode
) {}
