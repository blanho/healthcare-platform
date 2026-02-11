package com.healthcare.notification.domain;

public enum NotificationStatus {
    PENDING("Waiting to be sent"),
    SCHEDULED("Scheduled for future delivery"),
    SENDING("Currently being sent"),
    SENT("Successfully sent to delivery channel"),
    DELIVERED("Confirmed delivery to recipient"),
    READ("Recipient has read the notification"),
    FAILED("Delivery failed"),
    CANCELLED("Cancelled before sending");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canCancel() {
        return this == PENDING || this == SCHEDULED;
    }

    public boolean canRetry() {
        return this == FAILED;
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == READ || this == CANCELLED;
    }

    public boolean isSuccessful() {
        return this == SENT || this == DELIVERED || this == READ;
    }
}
