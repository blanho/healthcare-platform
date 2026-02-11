package com.healthcare.medicalrecord.domain;

public enum RecordType {

    VISIT_NOTE("Visit Note"),

    PROGRESS_NOTE("Progress Note"),

    HISTORY_PHYSICAL("History & Physical"),

    CONSULTATION("Consultation"),

    PROCEDURE_NOTE("Procedure Note"),

    DISCHARGE_SUMMARY("Discharge Summary"),

    LAB_RESULT("Lab Result"),

    IMAGING_REPORT("Imaging Report"),

    DIAGNOSIS("Diagnosis"),

    REFERRAL("Referral"),

    PRESCRIPTION("Prescription"),

    IMMUNIZATION("Immunization"),

    VITALS("Vital Signs"),

    NURSING_NOTE("Nursing Note"),

    OPERATIVE_REPORT("Operative Report");

    private final String displayName;

    RecordType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
