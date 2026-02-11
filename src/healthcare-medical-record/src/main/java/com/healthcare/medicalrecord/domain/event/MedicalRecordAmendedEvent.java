package com.healthcare.medicalrecord.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MedicalRecordAmendedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID medicalRecordId,
    String recordNumber,
    String reason,
    String amendedBy
) implements DomainEvent {

    public MedicalRecordAmendedEvent(
            UUID medicalRecordId,
            String recordNumber,
            String reason,
            String amendedBy) {
        this(UUID.randomUUID(), Instant.now(), medicalRecordId, recordNumber, reason, amendedBy);
    }

    @Override
    public UUID aggregateId() {
        return medicalRecordId;
    }

    @Override
    public String eventType() {
        return "medical_record.amended";
    }
}
