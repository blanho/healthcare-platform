package com.healthcare.appointment.domain.event;

import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentCancelledEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber,
    UUID patientId,
    UUID providerId,
    String reason
) implements DomainEvent {

    public AppointmentCancelledEvent(UUID appointmentId, String appointmentNumber,
                                     UUID patientId, UUID providerId, String reason) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber, patientId, providerId, reason);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.cancelled";
    }
}
