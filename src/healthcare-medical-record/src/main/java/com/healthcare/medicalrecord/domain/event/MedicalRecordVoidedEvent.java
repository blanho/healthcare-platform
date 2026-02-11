package com.healthcare.medicalrecord.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record MedicalRecordVoidedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID medicalRecordId,
    String recordNumber,
    String reason,
    String voidedBy
) implements DomainEvent {

    public MedicalRecordVoidedEvent(
            UUID medicalRecordId,
            String recordNumber,
            String reason,
            String voidedBy) {
        this(UUID.randomUUID(), Instant.now(), medicalRecordId, recordNumber, reason, voidedBy);
    }

    @Override
    public UUID aggregateId() {
        return medicalRecordId;
    }

    @Override
    public String eventType() {
        return "medical_record.voided";
    }
}
