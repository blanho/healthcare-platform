package com.healthcare.provider.constant;

public final class ProviderConstants {

    private ProviderConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final String PROVIDER_NUMBER_PREFIX = "PRV";
    public static final int PROVIDER_NUMBER_LENGTH = 10;

    public static final int NPI_LENGTH = 10;

    public static final int LICENSE_NUMBER_MIN_LENGTH = 6;
    public static final int LICENSE_NUMBER_MAX_LENGTH = 20;
    public static final int LICENSE_EXPIRY_WARNING_DAYS = 90;

    public static final int DEFAULT_WORK_START_HOUR = 8;
    public static final int DEFAULT_WORK_END_HOUR = 17;
    public static final int DEFAULT_SLOT_DURATION_MINUTES = 30;
    public static final int MAX_PATIENTS_PER_DAY = 20;

    public static final int JUNIOR_EXPERIENCE_YEARS = 5;
    public static final int SENIOR_EXPERIENCE_YEARS = 10;
    public static final int EXPERT_EXPERIENCE_YEARS = 20;

    public static final String MIN_CONSULTATION_FEE = "50.00";
    public static final String MAX_CONSULTATION_FEE = "1000.00";
}
