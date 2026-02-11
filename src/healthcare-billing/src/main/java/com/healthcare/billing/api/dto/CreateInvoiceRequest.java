package com.healthcare.billing.api.dto;

import com.healthcare.billing.domain.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    UUID appointmentId,

    @NotEmpty(message = "At least one item is required")
    @Valid
    List<InvoiceItemRequest> items,

    @PositiveOrZero(message = "Tax rate must be zero or positive")
    BigDecimal taxRate,

    @PositiveOrZero(message = "Discount amount must be zero or positive")
    BigDecimal discountAmount,

    @PositiveOrZero(message = "Discount percentage must be zero or positive")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    BigDecimal discountPercentage,

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    LocalDate dueDate,

    String notes,

    InsuranceInfoRequest insuranceInfo
) {
    public record InvoiceItemRequest(
        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @Size(max = 20, message = "Procedure code cannot exceed 20 characters")
        String procedureCode,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,

        @NotNull(message = "Unit price is required")
        @Positive(message = "Unit price must be positive")
        BigDecimal unitPrice
    ) {}

    public record InsuranceInfoRequest(
        @NotBlank(message = "Insurance provider is required")
        String provider,

        @NotBlank(message = "Policy number is required")
        String policyNumber,

        String groupNumber,

        String subscriberName,

        String subscriberId
    ) {}
}
