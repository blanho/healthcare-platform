package com.healthcare.patient.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PatientTransferredEvent(
    UUID eventId,
    Instant occurredAt,
    UUID patientId,
    String medicalRecordNumber,
    String newFacility
) implements DomainEvent {

    public PatientTransferredEvent(UUID patientId, String medicalRecordNumber, String newFacility) {
        this(UUID.randomUUID(), Instant.now(), patientId, medicalRecordNumber, newFacility);
    }

    @Override
    public UUID aggregateId() {
        return patientId;
    }

    @Override
    public String eventType() {
        return "patient.transferred";
    }
}
