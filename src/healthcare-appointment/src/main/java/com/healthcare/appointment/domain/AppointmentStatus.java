package com.healthcare.appointment.domain;

public enum AppointmentStatus {

    SCHEDULED("Scheduled"),

    CONFIRMED("Confirmed"),

    CHECKED_IN("Checked In"),

    IN_PROGRESS("In Progress"),

    COMPLETED("Completed"),

    CANCELLED("Cancelled"),

    NO_SHOW("No Show"),

    RESCHEDULED("Rescheduled");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canCheckIn() {
        return this == SCHEDULED || this == CONFIRMED;
    }

    public boolean canCancel() {
        return this == SCHEDULED || this == CONFIRMED;
    }

    public boolean canReschedule() {
        return this == SCHEDULED || this == CONFIRMED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == CONFIRMED || this == CHECKED_IN || this == IN_PROGRESS;
    }
}
