package com.healthcare.notification.service.channel;

import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.exception.NotificationDeliveryException;

public interface NotificationChannel {

    NotificationType getType();

    void send(Notification notification) throws NotificationDeliveryException;

    default boolean isAvailable() {
        return true;
    }
}
