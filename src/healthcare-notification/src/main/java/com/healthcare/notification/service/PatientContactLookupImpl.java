package com.healthcare.notification.service;

import com.healthcare.patient.service.PatientService;
import com.healthcare.patient.api.dto.PatientResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PatientContactLookupImpl implements PatientContactLookup {

    private final PatientService patientService;

    public PatientContactLookupImpl(PatientService patientService) {
        this.patientService = patientService;
    }

    @Override
    public Optional<PatientContact> findByPatientId(UUID patientId) {
        return patientService.getPatientById(patientId)
            .map(this::toPatientContact);
    }

    private PatientContact toPatientContact(PatientResponse patient) {
        String fullName = patient.firstName() + " " + patient.lastName();

        return new PatientContact(
            patient.id(),
            patient.id(),
            fullName,
            patient.email(),
            patient.phoneNumber()
        );
    }
}
