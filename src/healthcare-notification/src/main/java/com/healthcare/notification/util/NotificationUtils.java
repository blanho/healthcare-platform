package com.healthcare.notification.util;

public final class NotificationUtils {

    private NotificationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String truncateForSms(String message, int maxLength) {
        if (message == null || message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength - 3) + "...";
    }

    public static String formatPhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }

        String digits = phone.replaceAll("[^0-9]", "");

        if (digits.length() == 10) {
            return "+1" + digits;
        }
        if (digits.length() == 11 && digits.startsWith("1")) {
            return "+" + digits;
        }
        return phone;
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
