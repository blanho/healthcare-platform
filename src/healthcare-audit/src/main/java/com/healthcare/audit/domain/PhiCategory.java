package com.healthcare.audit.domain;

public enum PhiCategory {
    GENERAL("GENERAL", "General protected health information"),

    IDENTIFIER("IDENTIFIER", "Names, IDs, SSN, MRN"),

    CONTACT("CONTACT", "Address, phone, email, fax"),

    DATE("DATE", "Dates related to individual"),

    FINANCIAL("FINANCIAL", "Account numbers, payment info"),

    MEDICAL("MEDICAL", "Diagnosis, treatment, medications"),

    BIOMETRIC("BIOMETRIC", "Fingerprints, voice prints, DNA"),

    PHOTO("PHOTO", "Full face photos, comparable images"),

    DEVICE("DEVICE", "Device identifiers and serial numbers"),

    WEB("WEB", "IP addresses, URLs, web identifiers"),

    GENETIC("GENETIC", "Genetic test results and markers"),

    SUBSTANCE_ABUSE("SUBSTANCE_ABUSE", "Substance abuse treatment records"),

    MENTAL_HEALTH("MENTAL_HEALTH", "Mental health treatment records"),

    HIV("HIV", "HIV/AIDS diagnosis and treatment");

    private final String code;
    private final String description;

    PhiCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }

    public boolean requiresEnhancedProtection() {
        return this == SUBSTANCE_ABUSE ||
               this == MENTAL_HEALTH ||
               this == HIV ||
               this == GENETIC;
    }
}
