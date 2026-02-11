package com.healthcare.location.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {

    @Column(name = "street_address", length = 200)
    private String streetAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 50)
    private String country;

    public String formatFull() {
        return String.format("%s, %s, %s %s, %s",
            streetAddress != null ? streetAddress : "",
            city != null ? city : "",
            state != null ? state : "",
            postalCode != null ? postalCode : "",
            country != null ? country : ""
        ).replaceAll(", ,", ",").trim();
    }
}
