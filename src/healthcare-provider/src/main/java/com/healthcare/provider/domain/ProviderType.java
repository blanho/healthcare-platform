package com.healthcare.provider.domain;

public enum ProviderType {

    DOCTOR("Doctor", "MD"),
    SPECIALIST("Specialist", "SP"),
    NURSE_PRACTITIONER("Nurse Practitioner", "NP"),
    PHYSICIAN_ASSISTANT("Physician Assistant", "PA"),
    REGISTERED_NURSE("Registered Nurse", "RN"),
    LICENSED_PRACTICAL_NURSE("Licensed Practical Nurse", "LPN"),
    DENTIST("Dentist", "DDS"),
    PHARMACIST("Pharmacist", "PharmD"),
    PSYCHOLOGIST("Psychologist", "PhD"),
    THERAPIST("Therapist", "LCSW"),
    RADIOLOGIST("Radiologist", "RAD"),
    SURGEON("Surgeon", "SURG");

    private final String displayName;
    private final String code;

    ProviderType(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}
