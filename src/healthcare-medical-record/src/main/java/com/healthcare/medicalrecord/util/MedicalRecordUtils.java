package com.healthcare.medicalrecord.util;

import com.healthcare.medicalrecord.domain.VitalSigns;

public final class MedicalRecordUtils {

    private MedicalRecordUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static boolean areVitalsNormal(VitalSigns vitals) {
        if (vitals == null) {
            return false;
        }

        if (vitals.getSystolicBp() != null) {
            int systolic = vitals.getSystolicBp();
            if (systolic < 90 || systolic > 120) {
                return false;
            }
        }

        if (vitals.getHeartRate() != null) {
            int hr = vitals.getHeartRate();
            if (hr < 60 || hr > 100) {
                return false;
            }
        }

        if (vitals.getTemperature() != null) {
            double temp = vitals.getTemperature().doubleValue();
            if (temp < 36.1 || temp > 37.2) {
                return false;
            }
        }

        return true;
    }

    public static boolean areVitalsCritical(VitalSigns vitals) {
        if (vitals == null) {
            return false;
        }

        if (vitals.getSystolicBp() != null) {
            int systolic = vitals.getSystolicBp();
            if (systolic > 180 || systolic < 70) {
                return true;
            }
        }

        if (vitals.getHeartRate() != null) {
            int hr = vitals.getHeartRate();
            if (hr > 120 || hr < 50) {
                return true;
            }
        }

        if (vitals.getTemperature() != null) {
            double temp = vitals.getTemperature().doubleValue();
            if (temp > 39.0 || temp < 35.0) {
                return true;
            }
        }

        return false;
    }
}
