package com.healthcare.patient.api.dto;

import com.healthcare.patient.domain.PatientStatus;

public record PatientSearchCriteria(
    String name,
    String email,
    String phoneNumber,
    String medicalRecordNumber,
    PatientStatus status
) {

    public boolean hasAnyCriteria() {
        return (name != null && !name.isBlank()) ||
               (email != null && !email.isBlank()) ||
               (phoneNumber != null && !phoneNumber.isBlank()) ||
               (medicalRecordNumber != null && !medicalRecordNumber.isBlank()) ||
               status != null;
    }
}
