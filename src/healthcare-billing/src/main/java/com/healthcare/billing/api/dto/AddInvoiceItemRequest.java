package com.healthcare.billing.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddInvoiceItemRequest(
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
