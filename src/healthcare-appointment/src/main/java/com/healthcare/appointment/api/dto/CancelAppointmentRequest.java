package com.healthcare.appointment.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelAppointmentRequest(

    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 1000, message = "Cancellation reason must be at most 1000 characters")
    String reason,

    boolean cancelledByPatient
) {}
