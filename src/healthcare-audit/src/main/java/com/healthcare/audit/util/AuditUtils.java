package com.healthcare.audit.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class AuditUtils {

    private AuditUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String hashSensitiveData(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        return parts[0].substring(0, Math.min(2, parts[0].length())) + "***@" + parts[1];
    }

    public static String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) {
            return "***";
        }
        return "***-***-" + phone.substring(phone.length() - 4);
    }
}
