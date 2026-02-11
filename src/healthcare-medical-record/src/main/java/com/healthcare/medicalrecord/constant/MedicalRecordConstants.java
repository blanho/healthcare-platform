package com.healthcare.medicalrecord.constant;

public final class MedicalRecordConstants {

    private MedicalRecordConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int BLOOD_PRESSURE_SYSTOLIC_MIN = 90;
    public static final int BLOOD_PRESSURE_SYSTOLIC_MAX = 120;
    public static final int BLOOD_PRESSURE_DIASTOLIC_MIN = 60;
    public static final int BLOOD_PRESSURE_DIASTOLIC_MAX = 80;

    public static final int HEART_RATE_MIN = 60;
    public static final int HEART_RATE_MAX = 100;

    public static final double TEMPERATURE_MIN_CELSIUS = 36.1;
    public static final double TEMPERATURE_MAX_CELSIUS = 37.2;

    public static final int RESPIRATORY_RATE_MIN = 12;
    public static final int RESPIRATORY_RATE_MAX = 20;

    public static final int OXYGEN_SATURATION_MIN = 95;
    public static final int OXYGEN_SATURATION_MAX = 100;

    public static final int RECORD_RETENTION_YEARS = 7;

    public static final String RECORD_NUMBER_PREFIX = "MR";
    public static final int RECORD_NUMBER_LENGTH = 12;
}
