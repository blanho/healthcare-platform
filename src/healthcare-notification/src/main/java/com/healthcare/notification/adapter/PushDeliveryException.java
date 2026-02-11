package com.healthcare.notification.adapter;

public class PushDeliveryException extends RuntimeException {

    public PushDeliveryException(String message) {
        super(message);
    }

    public PushDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
