package com.healthcare.notification.exception;

public class NotificationDeliveryException extends RuntimeException {

    private final boolean retryable;

    private NotificationDeliveryException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
    }

    private NotificationDeliveryException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public static NotificationDeliveryException retryable(String message) {
        return new NotificationDeliveryException(message, true);
    }

    public static NotificationDeliveryException retryable(String message, Throwable cause) {
        return new NotificationDeliveryException(message, cause, true);
    }

    public static NotificationDeliveryException permanent(String message) {
        return new NotificationDeliveryException(message, false);
    }

    public static NotificationDeliveryException permanent(String message, Throwable cause) {
        return new NotificationDeliveryException(message, cause, false);
    }

    public static NotificationDeliveryException emailFailed(String reason) {
        return new NotificationDeliveryException("Email delivery failed: " + reason, true);
    }

    public static NotificationDeliveryException smsFailed(String reason) {
        return new NotificationDeliveryException("SMS delivery failed: " + reason, true);
    }

    public static NotificationDeliveryException pushFailed(String reason) {
        return new NotificationDeliveryException("Push notification failed: " + reason, true);
    }

    public static NotificationDeliveryException invalidRecipient() {
        return new NotificationDeliveryException("Invalid or missing recipient information", false);
    }
}
