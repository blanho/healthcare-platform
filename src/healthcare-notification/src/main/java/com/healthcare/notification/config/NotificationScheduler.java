package com.healthcare.notification.config;

import com.healthcare.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;

@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    private final NotificationService notificationService;
    private final NotificationProperties properties;

    public NotificationScheduler(
            NotificationService notificationService,
            NotificationProperties properties) {
        this.notificationService = notificationService;
        this.properties = properties;
    }

    @Scheduled(fixedRateString = "${healthcare.notification.processing-interval-seconds:60}000")
    public void processPendingNotifications() {
        log.debug("Running scheduled pending notification processing");
        try {
            notificationService.processPendingNotifications();
        } catch (Exception e) {
            log.error("Error processing pending notifications", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void retryFailedNotifications() {
        log.debug("Running scheduled failed notification retry");
        try {
            notificationService.retryFailedNotifications(properties.maxRetries());
        } catch (Exception e) {
            log.error("Error retrying failed notifications", e);
        }
    }
}
