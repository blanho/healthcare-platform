package com.healthcare.notification.domain;

public enum NotificationType {
    EMAIL("Email notification"),
    SMS("SMS text message"),
    PUSH("Push notification to mobile app"),
    IN_APP("In-application notification");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
