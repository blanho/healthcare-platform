package com.healthcare.provider.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MedicalLicense {

    @Column(name = "license_number", nullable = false, unique = true, length = 100)
    private String licenseNumber;

    @Column(name = "license_state", nullable = false, length = 50)
    private String licenseState;

    @Column(name = "license_expiry", nullable = false)
    private LocalDate expiryDate;

    public boolean isValid() {
        return expiryDate != null && !expiryDate.isBefore(LocalDate.now());
    }

    public boolean expiresWithinDays(int days) {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now().plusDays(days));
    }

    public long daysUntilExpiry() {
        if (expiryDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalLicense that = (MedicalLicense) o;
        return Objects.equals(licenseNumber, that.licenseNumber) &&
               Objects.equals(licenseState, that.licenseState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(licenseNumber, licenseState);
    }

    @Override
    public String toString() {
        return licenseNumber + " (" + licenseState + ")";
    }
}
