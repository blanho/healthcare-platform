package com.healthcare.notification.service;

import java.util.Optional;
import java.util.UUID;

public interface PatientContactLookup {

    Optional<PatientContact> findByPatientId(UUID patientId);

    record PatientContact(
        UUID userId,
        UUID patientId,
        String name,
        String email,
        String phoneNumber
    ) {}
}
