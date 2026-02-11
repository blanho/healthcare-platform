package com.healthcare.appointment.domain.event;

import com.healthcare.appointment.domain.TimeSlot;
import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentScheduledEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber,
    UUID patientId,
    UUID providerId,
    TimeSlot timeSlot
) implements DomainEvent {

    public AppointmentScheduledEvent(UUID appointmentId, String appointmentNumber,
                                     UUID patientId, UUID providerId, TimeSlot timeSlot) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber, patientId, providerId, timeSlot);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.scheduled";
    }
}
