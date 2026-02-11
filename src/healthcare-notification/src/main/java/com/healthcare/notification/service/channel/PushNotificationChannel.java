package com.healthcare.notification.service.channel;

import com.healthcare.notification.adapter.PushNotificationGateway;
import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationRecipient;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class PushNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationChannel.class);

    private final Optional<PushNotificationGateway> pushGateway;

    public PushNotificationChannel(@Autowired(required = false) PushNotificationGateway pushGateway) {
        this.pushGateway = Optional.ofNullable(pushGateway);
        log.info("Push channel initialized with gateway: {}",
            pushGateway != null ? pushGateway.getClass().getSimpleName() : "NONE (simulation mode)");
    }

    @Override
    public NotificationType getType() {
        return NotificationType.PUSH;
    }

    @Override
    public void send(Notification notification) throws NotificationDeliveryException {
        NotificationRecipient recipient = notification.getRecipient();

        if (recipient == null || !recipient.hasDeviceToken()) {
            throw NotificationDeliveryException.invalidRecipient();
        }

        try {

            log.info("Sending push notification: id={}, category={}",
                notification.getId(), notification.getCategory());

            if (pushGateway.isPresent() && pushGateway.get().isAvailable()) {

                Map<String, String> data = new HashMap<>();
                data.put("notificationId", notification.getId().toString());
                data.put("category", notification.getCategory().name());

                pushGateway.get().sendPushNotification(
                    recipient.deviceToken(),
                    notification.getTitle(),
                    notification.getMessage(),
                    data
                );
            } else {

                log.info("Push sent (simulation mode - no gateway configured): id={}", notification.getId());
            }

        } catch (Exception e) {
            log.error("Failed to send push notification: notificationId={}", notification.getId(), e);
            throw NotificationDeliveryException.pushFailed(e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return pushGateway.map(PushNotificationGateway::isAvailable).orElse(true);
    }
}
