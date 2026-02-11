package com.healthcare.notification.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "notification.sms.aws-sns", name = "enabled", havingValue = "true")
public class AwsSnsAdapter implements SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(AwsSnsAdapter.class);

    private final String region;
    private final String accessKeyId;
    private final String secretAccessKey;

    public AwsSnsAdapter(
            @Value("${notification.sms.aws-sns.region:us-east-1}") String region,
            @Value("${notification.sms.aws-sns.access-key-id:}") String accessKeyId,
            @Value("${notification.sms.aws-sns.secret-access-key:}") String secretAccessKey) {
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
        log.info("AWS SNS adapter initialized for region: {}", region);
    }

    @Override
    public void sendSms(String toPhoneNumber, String message) {
        log.debug("Sending SMS via AWS SNS to: {}", maskPhoneNumber(toPhoneNumber));

        try {

            log.info("SMS sent (simulated) via AWS SNS to: {}, length: {} chars",
                maskPhoneNumber(toPhoneNumber), message.length());

        } catch (Exception e) {
            log.error("Failed to send SMS via AWS SNS to: {}", maskPhoneNumber(toPhoneNumber), e);
            throw new SmsDeliveryException("AWS SNS delivery failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        return accessKeyId != null && !accessKeyId.isEmpty()
            && secretAccessKey != null && !secretAccessKey.isEmpty();
    }

    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***-***-" + phone.substring(phone.length() - 4);
    }
}
