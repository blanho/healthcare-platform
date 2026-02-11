package com.healthcare.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Pattern;

public class PiiMaskingConverter extends ClassicConverter {

    private static final Pattern SSN_PATTERN =
        Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final String SSN_MASK = "***-**-****";

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final String EMAIL_MASK = "***@***.***";

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("(\\+1)?[\\s.-]?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}");
    private static final String PHONE_MASK = "***-***-****";

    private static final Pattern CREDIT_CARD_PATTERN =
        Pattern.compile("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b");
    private static final String CREDIT_CARD_MASK = "****-****-****-****";

    private static final Pattern MRN_PATTERN =
        Pattern.compile("\\bMRN[-]?[A-Z0-9]{6,12}\\b", Pattern.CASE_INSENSITIVE);
    private static final String MRN_MASK = "MRN-********";

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null) {
            return "";
        }
        return maskPii(message);
    }

    public static String maskPii(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        String masked = message;

        masked = SSN_PATTERN.matcher(masked).replaceAll(SSN_MASK);
        masked = EMAIL_PATTERN.matcher(masked).replaceAll(EMAIL_MASK);
        masked = PHONE_PATTERN.matcher(masked).replaceAll(PHONE_MASK);
        masked = CREDIT_CARD_PATTERN.matcher(masked).replaceAll(CREDIT_CARD_MASK);
        masked = MRN_PATTERN.matcher(masked).replaceAll(MRN_MASK);

        return masked;
    }
}
