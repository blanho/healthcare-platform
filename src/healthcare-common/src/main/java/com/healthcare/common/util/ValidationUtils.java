package com.healthcare.common.util;

import java.util.UUID;
import java.util.regex.Pattern;

import com.healthcare.common.exception.BusinessRuleViolationException;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );

    private ValidationUtils() {

    }

    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new BusinessRuleViolationException(
                String.format("%s is required", fieldName)
            );
        }
        return value;
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessRuleViolationException(
                String.format("%s is required and cannot be blank", fieldName)
            );
        }
        return value;
    }

    public static String validateEmail(String email) {
        requireNonBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessRuleViolationException("Invalid email format");
        }
        return email.toLowerCase();
    }

    public static String validatePhoneNumber(String phone) {
        requireNonBlank(phone, "Phone number");
        String sanitized = phone.replaceAll("[\\s\\-()]", "");
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new BusinessRuleViolationException("Invalid phone number format");
        }
        return sanitized;
    }

    public static UUID requireValidId(UUID id, String fieldName) {
        return requireNonNull(id, fieldName);
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new BusinessRuleViolationException(
                String.format("%s must be positive", fieldName)
            );
        }
        return value;
    }

    public static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new BusinessRuleViolationException(
                String.format("%s cannot be negative", fieldName)
            );
        }
        return value;
    }

    public static String requireLength(String value, String fieldName, int min, int max) {
        requireNonBlank(value, fieldName);
        int length = value.length();
        if (length < min || length > max) {
            throw new BusinessRuleViolationException(
                String.format("%s must be between %d and %d characters", fieldName, min, max)
            );
        }
        return value;
    }
}
