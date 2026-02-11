package com.healthcare.notification.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "notification.sms.twilio", name = "enabled", havingValue = "true")
public class TwilioSmsAdapter implements SmsGateway {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsAdapter.class);

    private final String accountSid;
    private final String authToken;
    private final String fromNumber;

    public TwilioSmsAdapter(
            @Value("${notification.sms.twilio.account-sid:}") String accountSid,
            @Value("${notification.sms.twilio.auth-token:}") String authToken,
            @Value("${notification.sms.twilio.from-number:}") String fromNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        log.info("Twilio SMS adapter initialized with from number: {}", maskPhoneNumber(fromNumber));
    }

    @Override
    public void sendSms(String toPhoneNumber, String message) {
        log.debug("Sending SMS via Twilio to: {}", maskPhoneNumber(toPhoneNumber));

        try {

            log.info("SMS sent (simulated) to: {}, length: {} chars",
                maskPhoneNumber(toPhoneNumber), message.length());

        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio to: {}", maskPhoneNumber(toPhoneNumber), e);
            throw new SmsDeliveryException("Twilio SMS delivery failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        return accountSid != null && !accountSid.isEmpty()
            && authToken != null && !authToken.isEmpty();
    }

    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***-***-" + phone.substring(phone.length() - 4);
    }
}
