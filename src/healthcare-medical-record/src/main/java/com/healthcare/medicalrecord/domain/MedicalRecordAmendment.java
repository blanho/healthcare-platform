package com.healthcare.medicalrecord.domain;

import java.time.Instant;
import java.util.UUID;

public record MedicalRecordAmendment(
    UUID id,
    UUID medicalRecordId,
    String reason,
    String amendedBy,
    Instant amendedAt
) {
    public MedicalRecordAmendment(UUID medicalRecordId, String reason, String amendedBy) {
        this(UUID.randomUUID(), medicalRecordId, reason, amendedBy, Instant.now());
    }
}
