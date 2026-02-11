package com.healthcare.notification.constant;

public final class NotificationConstants {

    private NotificationConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int SMS_MAX_LENGTH = 160;
    public static final int SMS_LONG_MAX_LENGTH = 1600;

    public static final String DEFAULT_FROM_EMAIL = "noreply@healthcare-platform.com";
    public static final String DEFAULT_FROM_NAME = "Healthcare Platform";

    public static final int MAX_RETRY_ATTEMPTS = 3;
    public static final int RETRY_DELAY_SECONDS = 60;

    public static final int NOTIFICATION_RETENTION_DAYS = 90;
    public static final int UNREAD_NOTIFICATION_EXPIRY_DAYS = 30;

    public static final String PRIORITY_LOW = "LOW";
    public static final String PRIORITY_NORMAL = "NORMAL";
    public static final String PRIORITY_HIGH = "HIGH";
    public static final String PRIORITY_URGENT = "URGENT";
}
