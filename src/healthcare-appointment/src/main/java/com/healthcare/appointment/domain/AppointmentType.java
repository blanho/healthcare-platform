package com.healthcare.appointment.domain;

public enum AppointmentType {

    CONSULTATION("Consultation", 30),
    FOLLOW_UP("Follow-up", 15),
    ANNUAL_CHECKUP("Annual Checkup", 45),
    EMERGENCY("Emergency", 60),
    PROCEDURE("Procedure", 60),
    LAB_WORK("Lab Work", 15),
    VACCINATION("Vaccination", 15),
    TELEHEALTH("Telehealth", 30),
    SPECIALIST_REFERRAL("Specialist Referral", 45),
    MENTAL_HEALTH("Mental Health", 60),
    PHYSICAL_THERAPY("Physical Therapy", 45),
    PRENATAL("Prenatal Visit", 30),
    PEDIATRIC("Pediatric Visit", 30);

    private final String displayName;
    private final int defaultDurationMinutes;

    AppointmentType(String displayName, int defaultDurationMinutes) {
        this.displayName = displayName;
        this.defaultDurationMinutes = defaultDurationMinutes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDefaultDurationMinutes() {
        return defaultDurationMinutes;
    }
}
