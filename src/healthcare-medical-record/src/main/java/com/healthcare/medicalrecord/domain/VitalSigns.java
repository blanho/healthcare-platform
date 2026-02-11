package com.healthcare.medicalrecord.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VitalSigns {

    @Column(name = "systolic_bp")
    private Integer systolicBp;

    @Column(name = "diastolic_bp")
    private Integer diastolicBp;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "respiratory_rate")
    private Integer respiratoryRate;

    @Column(name = "temperature", precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(name = "oxygen_saturation")
    private Integer oxygenSaturation;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 5, scale = 1)
    private BigDecimal heightCm;

    @Column(name = "pain_level")
    private Integer painLevel;

    @Column(name = "vitals_recorded_at")
    private Instant recordedAt;

    public BigDecimal calculateBmi() {
        if (weightKg == null || heightCm == null || heightCm.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP);
        return weightKg.divide(heightM.multiply(heightM), 1, java.math.RoundingMode.HALF_UP);
    }

    public String getBloodPressure() {
        if (systolicBp == null || diastolicBp == null) {
            return null;
        }
        return systolicBp + "/" + diastolicBp + " mmHg";
    }

    public boolean isBloodPressureElevated() {
        return systolicBp != null && diastolicBp != null
                && (systolicBp > 120 || diastolicBp > 80);
    }

    public boolean hasCriticalValue() {

        if (systolicBp != null && (systolicBp < 90 || systolicBp > 180)) return true;
        if (diastolicBp != null && (diastolicBp < 60 || diastolicBp > 120)) return true;
        if (heartRate != null && (heartRate < 40 || heartRate > 150)) return true;
        if (respiratoryRate != null && (respiratoryRate < 8 || respiratoryRate > 30)) return true;
        if (temperature != null && (temperature.compareTo(BigDecimal.valueOf(35)) < 0
                || temperature.compareTo(BigDecimal.valueOf(40)) > 0)) return true;
        if (oxygenSaturation != null && oxygenSaturation < 90) return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VitalSigns that = (VitalSigns) o;
        return Objects.equals(systolicBp, that.systolicBp) &&
               Objects.equals(diastolicBp, that.diastolicBp) &&
               Objects.equals(heartRate, that.heartRate) &&
               Objects.equals(temperature, that.temperature) &&
               Objects.equals(recordedAt, that.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systolicBp, diastolicBp, heartRate, temperature, recordedAt);
    }
}
