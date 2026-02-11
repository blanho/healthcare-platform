package com.healthcare.medicalrecord.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Diagnosis {

    @Column(name = "diagnosis_code", length = 10)
    private String code;

    @Column(name = "diagnosis_description", length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "diagnosis_type", length = 30)
    private DiagnosisType type;

    @Column(name = "is_primary")
    private boolean primary;

    @Column(name = "onset_date")
    private LocalDate onsetDate;

    @Column(name = "resolved_date")
    private LocalDate resolvedDate;

    @Column(name = "diagnosis_notes", length = 1000)
    private String notes;

    public static Diagnosis primary(String code, String description, DiagnosisType type) {
        return Diagnosis.builder()
                .code(code)
                .description(description)
                .type(type)
                .primary(true)
                .onsetDate(LocalDate.now())
                .build();
    }

    public static Diagnosis secondary(String code, String description, DiagnosisType type) {
        return Diagnosis.builder()
                .code(code)
                .description(description)
                .type(type)
                .primary(false)
                .onsetDate(LocalDate.now())
                .build();
    }

    public boolean isResolved() {
        return resolvedDate != null;
    }

    public boolean isChronic() {
        if (onsetDate == null) return false;
        return onsetDate.plusMonths(3).isBefore(LocalDate.now()) && !isResolved();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Diagnosis diagnosis = (Diagnosis) o;
        return Objects.equals(code, diagnosis.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " - " + description;
    }

    public enum DiagnosisType {
        CONFIRMED,

        WORKING,

        DIFFERENTIAL,

        RULED_OUT,

        ADMITTING,

        DISCHARGE
    }
}
