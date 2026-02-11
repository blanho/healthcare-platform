package com.healthcare.medicalrecord.domain.event;

import com.healthcare.common.domain.DomainEvent;
import com.healthcare.medicalrecord.domain.RecordType;

import java.time.Instant;
import java.util.UUID;

public record MedicalRecordCreatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID medicalRecordId,
    String recordNumber,
    UUID patientId,
    UUID providerId,
    RecordType recordType
) implements DomainEvent {

    public MedicalRecordCreatedEvent(
            UUID medicalRecordId,
            String recordNumber,
            UUID patientId,
            UUID providerId,
            RecordType recordType) {
        this(UUID.randomUUID(), Instant.now(), medicalRecordId, recordNumber, patientId, providerId, recordType);
    }

    @Override
    public UUID aggregateId() {
        return medicalRecordId;
    }

    @Override
    public String eventType() {
        return "medical_record.created";
    }
}
