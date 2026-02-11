package com.healthcare.patient.domain;

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
public class Insurance {

    @Column(name = "insurance_provider")
    private String providerName;

    @Column(name = "insurance_policy_number")
    private String policyNumber;

    @Column(name = "insurance_group_number")
    private String groupNumber;

    @Column(name = "insurance_holder_name")
    private String holderName;

    @Column(name = "insurance_holder_relationship")
    private String holderRelationship;

    @Column(name = "insurance_effective_date")
    private LocalDate effectiveDate;

    @Column(name = "insurance_expiration_date")
    private LocalDate expirationDate;

    public boolean isActive() {
        LocalDate today = LocalDate.now();
        boolean afterEffective = effectiveDate == null || !today.isBefore(effectiveDate);
        boolean beforeExpiration = expirationDate == null || !today.isAfter(expirationDate);
        return afterEffective && beforeExpiration;
    }

    public boolean isExpired() {
        return expirationDate != null && LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insurance insurance = (Insurance) o;
        return Objects.equals(providerName, insurance.providerName)
            && Objects.equals(policyNumber, insurance.policyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerName, policyNumber);
    }
}
