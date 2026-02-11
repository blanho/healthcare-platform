package com.healthcare.medicalrecord.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateMedicalRecordRequest(

    @Size(max = 2000, message = "Chief complaint must be at most 2000 characters")
    String chiefComplaint,

    @Size(max = 5000, message = "Notes must be at most 5000 characters")
    String notes,

    @Valid
    VitalSignsRequest vitalSigns,

    @Valid
    SoapNoteRequest soapNote,

    @Valid
    List<DiagnosisRequest> diagnoses
) {}
