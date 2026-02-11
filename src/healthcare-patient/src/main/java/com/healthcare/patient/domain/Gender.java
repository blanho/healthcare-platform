package com.healthcare.patient.domain;

public enum Gender {

    MALE("M", "Male"),
    FEMALE("F", "Female"),
    OTHER("O", "Other"),
    UNKNOWN("U", "Unknown"),
    PREFER_NOT_TO_SAY("X", "Prefer not to say");

    private final String code;
    private final String displayName;

    Gender(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Gender fromCode(String code) {
        for (Gender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        return UNKNOWN;
    }
}
