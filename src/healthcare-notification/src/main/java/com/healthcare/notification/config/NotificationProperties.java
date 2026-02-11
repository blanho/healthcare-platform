package com.healthcare.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "healthcare.notification")
public record NotificationProperties(
    int maxRetries,

    int processingIntervalSeconds,

    int cleanupDays,

    EmailProperties email,

    SmsProperties sms,

    PushProperties push
) {
    public NotificationProperties {
        if (maxRetries <= 0) maxRetries = 3;
        if (processingIntervalSeconds <= 0) processingIntervalSeconds = 60;
        if (cleanupDays <= 0) cleanupDays = 90;
    }

    public record EmailProperties(
        String fromAddress,
        String fromName,
        boolean enabled
    ) {
        public EmailProperties {
            if (fromAddress == null) fromAddress = "noreply@healthcare.com";
            if (fromName == null) fromName = "Healthcare Platform";
        }
    }

    public record SmsProperties(
        String fromNumber,
        boolean enabled
    ) {}

    public record PushProperties(
        boolean enabled
    ) {}
}
