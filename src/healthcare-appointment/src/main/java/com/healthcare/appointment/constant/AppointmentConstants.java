package com.healthcare.appointment.constant;

public final class AppointmentConstants {

    private AppointmentConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 30;
    public static final int CONSULTATION_DURATION_MINUTES = 15;
    public static final int CHECKUP_DURATION_MINUTES = 45;
    public static final int PROCEDURE_DURATION_MINUTES = 60;

    public static final int REMINDER_HOURS_BEFORE = 24;
    public static final int URGENT_REMINDER_HOURS_BEFORE = 2;

    public static final int CANCELLATION_ALLOWED_HOURS_BEFORE = 24;
    public static final int NO_SHOW_THRESHOLD_MINUTES = 15;

    public static final String APPOINTMENT_NUMBER_PREFIX = "APT";
    public static final int APPOINTMENT_NUMBER_LENGTH = 10;

    public static final int BUSINESS_START_HOUR = 8;
    public static final int BUSINESS_END_HOUR = 18;
}
