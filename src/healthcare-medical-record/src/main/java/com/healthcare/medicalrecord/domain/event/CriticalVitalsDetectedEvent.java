package com.healthcare.medicalrecord.domain.event;

import com.healthcare.common.domain.DomainEvent;
import com.healthcare.medicalrecord.domain.VitalSigns;

import java.time.Instant;
import java.util.UUID;

public record CriticalVitalsDetectedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID medicalRecordId,
    String recordNumber,
    UUID patientId,
    VitalSigns vitalSigns
) implements DomainEvent {

    public CriticalVitalsDetectedEvent(
            UUID medicalRecordId,
            String recordNumber,
            UUID patientId,
            VitalSigns vitalSigns) {
        this(UUID.randomUUID(), Instant.now(), medicalRecordId, recordNumber, patientId, vitalSigns);
    }

    @Override
    public UUID aggregateId() {
        return medicalRecordId;
    }

    @Override
    public String eventType() {
        return "medical_record.critical_vitals_detected";
    }
}
