package com.healthcare.appointment.api;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentLookup {

    Optional<AppointmentInfo> findById(UUID appointmentId);

    Optional<AppointmentInfo> findByAppointmentNumber(String appointmentNumber);

    record AppointmentInfo(
        UUID appointmentId,
        String appointmentNumber,
        UUID patientId,
        UUID providerId,
        String providerName,
        UUID locationId,
        String locationName,
        Instant startTime,
        Instant endTime,
        String status
    ) {}
}
