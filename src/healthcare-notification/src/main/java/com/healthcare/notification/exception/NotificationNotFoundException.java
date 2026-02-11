package com.healthcare.notification.exception;

public class NotificationNotFoundException extends RuntimeException {

    private NotificationNotFoundException(String message) {
        super(message);
    }

    public static NotificationNotFoundException byId(Object id) {
        return new NotificationNotFoundException("Notification not found with id: " + id);
    }
}
