package com.healthcare.patient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {

    @Column(name = "address_street")
    private String street;

    @Column(name = "address_city")
    private String city;

    @Column(name = "address_state")
    private String state;

    @Column(name = "address_zip_code")
    private String zipCode;

    @Column(name = "address_country")
    private String country;

    public String getFormattedAddress() {
        return String.format("%s, %s, %s %s, %s",
            street, city, state, zipCode, country);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street)
            && Objects.equals(city, address.city)
            && Objects.equals(state, address.state)
            && Objects.equals(zipCode, address.zipCode)
            && Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zipCode, country);
    }
}
