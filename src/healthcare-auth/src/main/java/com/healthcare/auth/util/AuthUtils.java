package com.healthcare.auth.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class AuthUtils {

    private static final SecureRandom RANDOM = new SecureRandom();

    private AuthUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generateSecureToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String maskToken(String token) {
        if (token == null || token.length() < 8) {
            return "***";
        }
        return token.substring(0, 4) + "..." + token.substring(token.length() - 4);
    }

    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
