package com.healthcare.notification.domain;

public enum NotificationCategory {

    APPOINTMENT("Appointment related notification"),
    APPOINTMENT_REMINDER("Reminder about upcoming appointment"),
    APPOINTMENT_CONFIRMATION("Confirmation of scheduled appointment"),
    APPOINTMENT_CANCELLATION("Notification of cancelled appointment"),
    APPOINTMENT_RESCHEDULED("Notification of rescheduled appointment"),

    LAB_RESULT_READY("Lab results are available"),
    PRESCRIPTION_READY("Prescription is ready for pickup"),
    PRESCRIPTION_REFILL("Prescription refill reminder"),
    CRITICAL_VITALS("Critical vital signs detected"),

    MEDICAL_RECORD_SHARED("Medical record has been shared"),
    MEDICAL_RECORD_UPDATED("Medical record has been updated"),

    BILLING("Billing related notification"),
    INVOICE_GENERATED("New invoice generated"),
    PAYMENT_RECEIVED("Payment confirmation"),
    PAYMENT_DUE("Payment reminder"),
    PAYMENT_OVERDUE("Overdue payment notice"),

    WELCOME("Welcome to the platform"),
    PASSWORD_RESET("Password reset request"),
    ACCOUNT_VERIFICATION("Account verification"),
    SECURITY_ALERT("Security alert"),

    NEW_PATIENT_ASSIGNED("New patient assigned"),
    SCHEDULE_CHANGE("Schedule has been modified"),

    SYSTEM_MAINTENANCE("System maintenance notice"),
    GENERAL_ANNOUNCEMENT("General announcement");

    private final String description;

    NotificationCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUrgent() {
        return this == CRITICAL_VITALS || this == SECURITY_ALERT;
    }

    public boolean isClinical() {
        return this == LAB_RESULT_READY || this == PRESCRIPTION_READY
            || this == PRESCRIPTION_REFILL || this == CRITICAL_VITALS
            || this == MEDICAL_RECORD_SHARED || this == MEDICAL_RECORD_UPDATED;
    }
}
