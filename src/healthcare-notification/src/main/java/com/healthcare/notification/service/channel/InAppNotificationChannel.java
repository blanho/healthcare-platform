package com.healthcare.notification.service.channel;

import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(InAppNotificationChannel.class);

    @Override
    public NotificationType getType() {
        return NotificationType.IN_APP;
    }

    @Override
    public void send(Notification notification) throws NotificationDeliveryException {

        log.info("In-app notification marked for delivery: id={}, category={}",
            notification.getId(), notification.getCategory());

    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
