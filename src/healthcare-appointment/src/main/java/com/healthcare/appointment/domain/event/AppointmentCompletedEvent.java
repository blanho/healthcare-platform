package com.healthcare.appointment.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentCompletedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber,
    UUID patientId,
    UUID providerId
) implements DomainEvent {

    public AppointmentCompletedEvent(UUID appointmentId, String appointmentNumber,
                                     UUID patientId, UUID providerId) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber, patientId, providerId);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.completed";
    }
}
