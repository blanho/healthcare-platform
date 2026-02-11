package com.healthcare.appointment.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentNoShowEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber,
    UUID patientId
) implements DomainEvent {

    public AppointmentNoShowEvent(UUID appointmentId, String appointmentNumber, UUID patientId) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber, patientId);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.no_show";
    }
}
