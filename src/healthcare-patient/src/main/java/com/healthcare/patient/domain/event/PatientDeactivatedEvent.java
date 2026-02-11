package com.healthcare.patient.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PatientDeactivatedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID patientId,
    String medicalRecordNumber
) implements DomainEvent {

    public PatientDeactivatedEvent(UUID patientId, String medicalRecordNumber) {
        this(UUID.randomUUID(), Instant.now(), patientId, medicalRecordNumber);
    }

    @Override
    public UUID aggregateId() {
        return patientId;
    }

    @Override
    public String eventType() {
        return "patient.deactivated";
    }
}
