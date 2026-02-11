package com.healthcare.medicalrecord.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MedicalRecordFinalizedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID medicalRecordId,
    String recordNumber,
    UUID patientId,
    String finalizedBy
) implements DomainEvent {

    public MedicalRecordFinalizedEvent(
            UUID medicalRecordId,
            String recordNumber,
            UUID patientId,
            String finalizedBy) {
        this(UUID.randomUUID(), Instant.now(), medicalRecordId, recordNumber, patientId, finalizedBy);
    }

    @Override
    public UUID aggregateId() {
        return medicalRecordId;
    }

    @Override
    public String eventType() {
        return "medical_record.finalized";
    }
}
