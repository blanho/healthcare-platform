package com.healthcare.notification.adapter;

import java.util.Map;

public interface PushNotificationGateway {

    void sendPushNotification(String deviceToken, String title, String body, Map<String, String> data);

    boolean isAvailable();
}
