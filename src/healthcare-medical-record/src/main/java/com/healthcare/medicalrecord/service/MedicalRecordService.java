package com.healthcare.medicalrecord.service;

import com.healthcare.medicalrecord.api.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MedicalRecordService {

    MedicalRecordResponse create(CreateMedicalRecordRequest request);

    MedicalRecordResponse update(UUID recordId, UpdateMedicalRecordRequest request);

    MedicalRecordResponse getById(UUID recordId);

    MedicalRecordResponse getByRecordNumber(String recordNumber);

    Page<MedicalRecordSummaryResponse> search(MedicalRecordSearchCriteria criteria, Pageable pageable);

    Page<MedicalRecordSummaryResponse> getByPatient(UUID patientId, Pageable pageable);

    Page<MedicalRecordSummaryResponse> getByProvider(UUID providerId, Pageable pageable);

    List<MedicalRecordSummaryResponse> getByAppointment(UUID appointmentId);

    List<MedicalRecordSummaryResponse> getDraftRecords(UUID providerId);

    MedicalRecordResponse finalize(UUID recordId, String userId);

    MedicalRecordResponse amend(UUID recordId, AmendRecordRequest request, String userId);

    MedicalRecordResponse voidRecord(UUID recordId, VoidRecordRequest request, String userId);

    MedicalRecordResponse addVitals(UUID recordId, VitalSignsRequest request);

    MedicalRecordResponse updateSoapNote(UUID recordId, SoapNoteRequest request);

    MedicalRecordResponse addDiagnosis(UUID recordId, DiagnosisRequest request);

    List<MedicalRecordSummaryResponse> getPatientTimeline(UUID patientId, int limit);
}
