package com.healthcare.patient.service;

import com.healthcare.common.api.PageResponse;
import com.healthcare.patient.api.dto.CreatePatientRequest;
import com.healthcare.patient.api.dto.PatientResponse;
import com.healthcare.patient.api.dto.PatientSearchCriteria;
import com.healthcare.patient.api.dto.PatientSummaryResponse;
import com.healthcare.patient.api.dto.UpdatePatientRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest request);

    Optional<PatientResponse> getPatientById(UUID id);

    Optional<PatientResponse> getPatientByMrn(String mrn);

    PatientResponse updatePatient(UUID id, UpdatePatientRequest request);

    PatientResponse activatePatient(UUID id);

    PatientResponse deactivatePatient(UUID id);

    void deletePatient(UUID id);

    PageResponse<PatientSummaryResponse> listPatients(Pageable pageable);

    PageResponse<PatientSummaryResponse> searchPatients(PatientSearchCriteria criteria, Pageable pageable);

    boolean canScheduleAppointments(UUID id);

    long count();

    long countByStatus(com.healthcare.patient.domain.PatientStatus status);

    long countCreatedBefore(java.time.Instant instant);

    Optional<PatientResponse> findById(UUID id);
}
