package com.healthcare.patient.repository;

import com.healthcare.patient.domain.PatientStatus;

import java.time.LocalDate;
import java.util.UUID;

public interface PatientSummaryProjection {

    UUID getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    String getPhoneNumber();

    String getMedicalRecordNumber();

    LocalDate getDateOfBirth();

    PatientStatus getStatus();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}
