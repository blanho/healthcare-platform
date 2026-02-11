package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record RecordPaymentRequest(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    BigDecimal amount,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,

    String cardLastFour,

    String cardBrand,

    String notes
) {}
