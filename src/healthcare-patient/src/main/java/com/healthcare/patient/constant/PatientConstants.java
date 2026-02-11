package com.healthcare.patient.constant;

public final class PatientConstants {

    private PatientConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final String MRN_PREFIX = "MRN";
    public static final int MRN_LENGTH = 10;

    public static final int MINOR_AGE_THRESHOLD = 18;
    public static final int SENIOR_AGE_THRESHOLD = 65;
    public static final int PEDIATRIC_AGE_THRESHOLD = 12;

    public static final String PHONE_REGEX = "^\\+?[1-9]\\d{1,14}$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final int MAX_EMERGENCY_CONTACTS = 3;

    public static final int POLICY_NUMBER_MIN_LENGTH = 6;
    public static final int POLICY_NUMBER_MAX_LENGTH = 20;

    public static final int ZIPCODE_US_LENGTH = 5;
    public static final int ZIPCODE_US_PLUS4_LENGTH = 10;
}
