package com.healthcare.medicalrecord.api.dto;

import jakarta.validation.constraints.Size;

public record SoapNoteRequest(

    @Size(max = 5000, message = "Subjective must be at most 5000 characters")
    String subjective,

    @Size(max = 5000, message = "Objective must be at most 5000 characters")
    String objective,

    @Size(max = 5000, message = "Assessment must be at most 5000 characters")
    String assessment,

    @Size(max = 5000, message = "Plan must be at most 5000 characters")
    String plan
) {}
