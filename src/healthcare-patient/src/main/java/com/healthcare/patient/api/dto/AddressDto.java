package com.healthcare.patient.api.dto;

import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

public record AddressDto(

    @Size(max = 255, message = "Street must be at most 255 characters")
    String street,

    @Size(max = 100, message = "City must be at most 100 characters")
    String city,

    @Size(max = 50, message = "State must be at most 50 characters")
    String state,

    @Size(max = 20, message = "Zip code must be at most 20 characters")
    String zipCode,

    @Size(max = 50, message = "Country must be at most 50 characters")
    String country
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
