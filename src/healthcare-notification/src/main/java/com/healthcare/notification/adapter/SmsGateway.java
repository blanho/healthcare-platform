package com.healthcare.notification.adapter;

public interface SmsGateway {

    void sendSms(String toPhoneNumber, String message);

    boolean isAvailable();
}
