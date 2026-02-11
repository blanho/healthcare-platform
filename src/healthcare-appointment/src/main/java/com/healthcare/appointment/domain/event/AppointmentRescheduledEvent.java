package com.healthcare.appointment.domain.event;

import com.healthcare.appointment.domain.TimeSlot;
import com.healthcare.common.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record AppointmentRescheduledEvent(
    UUID eventId,
    Instant occurredAt,
    UUID appointmentId,
    String appointmentNumber,
    TimeSlot previousSlot,
    TimeSlot newSlot
) implements DomainEvent {

    public AppointmentRescheduledEvent(UUID appointmentId, String appointmentNumber,
                                       TimeSlot previousSlot, TimeSlot newSlot) {
        this(UUID.randomUUID(), Instant.now(), appointmentId, appointmentNumber, previousSlot, newSlot);
    }

    @Override
    public UUID aggregateId() {
        return appointmentId;
    }

    @Override
    public String eventType() {
        return "appointment.rescheduled";
    }
}
