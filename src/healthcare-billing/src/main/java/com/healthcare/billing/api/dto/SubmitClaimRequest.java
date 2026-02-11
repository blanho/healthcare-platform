package com.healthcare.billing.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SubmitClaimRequest(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,

    @NotBlank(message = "Insurance provider is required")
    String insuranceProvider,

    @NotBlank(message = "Policy number is required")
    @Size(max = 50, message = "Policy number cannot exceed 50 characters")
    String policyNumber,

    @Size(max = 50, message = "Group number cannot exceed 50 characters")
    String groupNumber,

    String subscriberName,

    @Size(max = 50, message = "Subscriber ID cannot exceed 50 characters")
    String subscriberId,

    @NotNull(message = "Billed amount is required")
    @Positive(message = "Billed amount must be positive")
    BigDecimal billedAmount,

    @NotNull(message = "Service date is required")
    @PastOrPresent(message = "Service date cannot be in the future")
    LocalDate serviceDate
) {}
