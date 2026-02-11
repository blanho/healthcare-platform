package com.healthcare.patient.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record EmergencyContactDto(

    @Size(max = 200, message = "Name must be at most 200 characters")
    String name,

    @Size(max = 50, message = "Relationship must be at most 50 characters")
    String relationship,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Email(message = "Invalid email format")
    String email
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
