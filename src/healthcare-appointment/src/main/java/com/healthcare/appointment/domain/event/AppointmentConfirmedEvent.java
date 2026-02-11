package com.healthcare.appointment.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentConfirmedEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber
) implements DomainEvent {

    public AppointmentConfirmedEvent(UUID appointmentId, String appointmentNumber) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.confirmed";
    }
}
