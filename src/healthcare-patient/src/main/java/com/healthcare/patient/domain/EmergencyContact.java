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
public class EmergencyContact {

    @Column(name = "emergency_contact_name")
    private String name;

    @Column(name = "emergency_contact_relationship")
    private String relationship;

    @Column(name = "emergency_contact_phone")
    private String phoneNumber;

    @Column(name = "emergency_contact_email")
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmergencyContact that = (EmergencyContact) o;
        return Objects.equals(name, that.name)
            && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }
}
