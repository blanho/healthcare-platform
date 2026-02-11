package com.healthcare.patient.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public record InsuranceDto(

    @Size(max = 200, message = "Provider name must be at most 200 characters")
    String providerName,

    @Size(max = 50, message = "Policy number must be at most 50 characters")
    String policyNumber,

    @Size(max = 50, message = "Group number must be at most 50 characters")
    String groupNumber,

    @Size(max = 200, message = "Holder name must be at most 200 characters")
    String holderName,

    @Size(max = 50, message = "Holder relationship must be at most 50 characters")
    String holderRelationship,

    LocalDate effectiveDate,

    @FutureOrPresent(message = "Expiration date must be in the present or future")
    LocalDate expirationDate,

    boolean isActive
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
