package com.healthcare.notification.service.channel;

import com.healthcare.notification.adapter.SmsGateway;
import com.healthcare.notification.domain.Notification;
import com.healthcare.notification.domain.NotificationRecipient;
import com.healthcare.notification.domain.NotificationType;
import com.healthcare.notification.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SmsNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationChannel.class);

    private final Optional<SmsGateway> smsGateway;

    public SmsNotificationChannel(@Autowired(required = false) SmsGateway smsGateway) {
        this.smsGateway = Optional.ofNullable(smsGateway);
        log.info("SMS channel initialized with gateway: {}",
            smsGateway != null ? smsGateway.getClass().getSimpleName() : "NONE (simulation mode)");
    }

    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }

    @Override
    public void send(Notification notification) throws NotificationDeliveryException {
        NotificationRecipient recipient = notification.getRecipient();

        if (recipient == null || !recipient.hasPhoneNumber()) {
            throw NotificationDeliveryException.invalidRecipient();
        }

        try {

            log.info("Sending SMS notification: id={}, category={}",
                notification.getId(), notification.getCategory());

            if (smsGateway.isPresent() && smsGateway.get().isAvailable()) {

                smsGateway.get().sendSms(recipient.phoneNumber(), notification.getMessage());
            } else {

                log.info("SMS sent (simulation mode - no gateway configured): id={}", notification.getId());
            }

        } catch (Exception e) {
            log.error("Failed to send SMS: notificationId={}", notification.getId(), e);
            throw NotificationDeliveryException.smsFailed(e.getMessage());
        }
    }

    @Override
    public boolean isAvailable() {
        return smsGateway.map(SmsGateway::isAvailable).orElse(true);
    }
}
