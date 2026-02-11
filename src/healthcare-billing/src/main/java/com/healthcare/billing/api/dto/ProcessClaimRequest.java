package com.healthcare.billing.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProcessClaimRequest(
    @NotBlank(message = "Action is required")
    String action,

    @PositiveOrZero(message = "Allowed amount must be zero or positive")
    BigDecimal allowedAmount,

    @PositiveOrZero(message = "Paid amount must be zero or positive")
    BigDecimal paidAmount,

    @PositiveOrZero(message = "Patient responsibility must be zero or positive")
    BigDecimal patientResponsibility,

    @PositiveOrZero(message = "Copay amount must be zero or positive")
    BigDecimal copayAmount,

    @PositiveOrZero(message = "Deductible amount must be zero or positive")
    BigDecimal deductibleAmount,

    @PositiveOrZero(message = "Coinsurance amount must be zero or positive")
    BigDecimal coinsuranceAmount,

    @Size(max = 20, message = "Denial code cannot exceed 20 characters")
    String denialCode,

    @Size(max = 500, message = "Denial reason cannot exceed 500 characters")
    String denialReason,

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    String notes,

    @Size(max = 100, message = "EOB reference cannot exceed 100 characters")
    String eobReference
) {
    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_PARTIALLY_APPROVE = "PARTIALLY_APPROVE";
    public static final String ACTION_DENY = "DENY";
    public static final String ACTION_REQUEST_INFO = "REQUEST_INFO";
}
