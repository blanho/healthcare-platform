package com.healthcare.audit.domain;

public enum ResourceCategory {

    PATIENT("PATIENT", "Patient demographic and clinical data", true),
    MEDICAL_RECORD("MEDICAL_RECORD", "Medical records and notes", true),
    PRESCRIPTION("PRESCRIPTION", "Prescription and medication data", true),
    LAB_RESULT("LAB_RESULT", "Laboratory results", true),
    DIAGNOSIS("DIAGNOSIS", "Diagnosis information", true),
    VITAL_SIGNS("VITAL_SIGNS", "Vital signs measurements", true),
    ALLERGY("ALLERGY", "Allergy information", true),
    IMMUNIZATION("IMMUNIZATION", "Immunization records", true),

    APPOINTMENT("APPOINTMENT", "Appointment scheduling", true),
    BILLING("BILLING", "Billing and financial data", true),
    INSURANCE("INSURANCE", "Insurance information", true),
    INVOICE("INVOICE", "Invoice data", true),
    PAYMENT("PAYMENT", "Payment transactions", true),
    CLAIM("CLAIM", "Insurance claims", true),

    PROVIDER("PROVIDER", "Healthcare provider data", false),
    SCHEDULE("SCHEDULE", "Provider schedules", false),

    USER("USER", "User accounts", false),
    ROLE("ROLE", "User roles", false),
    PERMISSION("PERMISSION", "User permissions", false),

    CONFIGURATION("CONFIGURATION", "System configuration", false),
    AUDIT("AUDIT", "Audit logs", false),
    REPORT("REPORT", "Generated reports", true);

    private final String code;
    private final String description;
    private final boolean containsPhi;

    ResourceCategory(String code, String description, boolean containsPhi) {
        this.code = code;
        this.description = description;
        this.containsPhi = containsPhi;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
    public boolean containsPhi() { return containsPhi; }
}
