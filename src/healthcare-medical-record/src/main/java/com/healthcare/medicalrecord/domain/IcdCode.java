package com.healthcare.medicalrecord.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IcdCode {

    private static final Pattern ICD10_PATTERN = Pattern.compile("^[A-Z]\\d{2}(\\.\\d{1,4}[A-Z]?)?$");

    @Column(name = "icd_code", length = 10)
    private String code;

    @Column(name = "icd_description", length = 500)
    private String description;

    public static IcdCode of(String code, String description) {
        Objects.requireNonNull(code, "ICD code is required");
        String normalizedCode = code.toUpperCase().trim();

        if (!ICD10_PATTERN.matcher(normalizedCode).matches()) {
            throw new IllegalArgumentException("Invalid ICD-10 code format: " + code);
        }

        return new IcdCode(normalizedCode, description);
    }

    public static IcdCode of(String code) {
        return of(code, null);
    }

    public String getCategory() {
        return code.length() >= 3 ? code.substring(0, 3) : code;
    }

    public boolean isBillable() {
        return code.contains(".");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IcdCode icdCode = (IcdCode) o;
        return Objects.equals(code, icdCode.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + (description != null ? " - " + description : "");
    }
}
