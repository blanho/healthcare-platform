package com.healthcare.notification.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "notification.push.firebase", name = "enabled", havingValue = "true")
public class FirebasePushAdapter implements PushNotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(FirebasePushAdapter.class);

    private final String projectId;
    private final String credentialsPath;

    public FirebasePushAdapter(
            @Value("${notification.push.firebase.project-id:}") String projectId,
            @Value("${notification.push.firebase.credentials-path:}") String credentialsPath) {
        this.projectId = projectId;
        this.credentialsPath = credentialsPath;
        log.info("Firebase push adapter initialized for project: {}", projectId);
    }

    @Override
    public void sendPushNotification(String deviceToken, String title, String body, Map<String, String> data) {
        log.debug("Sending push notification via Firebase");

        try {

            log.info("Push notification sent (simulated) via Firebase: title={}, bodyLength={}",
                title, body.length());

        } catch (Exception e) {
            log.error("Failed to send push notification via Firebase", e);
            throw new PushDeliveryException("Firebase push delivery failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        return projectId != null && !projectId.isEmpty()
            && credentialsPath != null && !credentialsPath.isEmpty();
    }
}
