package com.healthcare.location.domain;

public enum LocationType {
    HOSPITAL("Hospital - Full service facility"),
    CLINIC("Clinic - Outpatient care facility"),
    URGENT_CARE("Urgent Care - Walk-in emergency care"),
    LABORATORY("Laboratory - Diagnostic testing facility"),
    PHARMACY("Pharmacy - Medication dispensary"),
    IMAGING_CENTER("Imaging Center - Radiology and imaging"),
    SURGERY_CENTER("Surgery Center - Surgical procedures"),
    REHABILITATION("Rehabilitation - Physical therapy and recovery");

    private final String description;

    LocationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
