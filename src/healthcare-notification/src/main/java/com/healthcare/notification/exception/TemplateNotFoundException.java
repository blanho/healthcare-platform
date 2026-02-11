package com.healthcare.notification.exception;

public class TemplateNotFoundException extends RuntimeException {

    private TemplateNotFoundException(String message) {
        super(message);
    }

    public static TemplateNotFoundException byCode(String code) {
        return new TemplateNotFoundException("Notification template not found with code: " + code);
    }

    public static TemplateNotFoundException byId(Object id) {
        return new TemplateNotFoundException("Notification template not found with id: " + id);
    }
}
