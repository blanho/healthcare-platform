package com.healthcare.auth.service;

import java.util.UUID;

public interface PasswordService {

    void forgotPassword(String email, String ipAddress, String userAgent);

    void resetPassword(String token, String newPassword);

    void changePassword(UUID userId, String currentPassword, String newPassword);

    void adminResetPassword(UUID userId, UUID adminUserId, boolean requireChange);

    boolean wasPasswordUsedRecently(UUID userId, String password);

    PasswordStrength validatePasswordStrength(String password);

    record PasswordStrength(
        boolean valid,
        int score,
        String message
    ) {
        public static PasswordStrength valid(int score) {
            return new PasswordStrength(true, score, "Password is strong");
        }

        public static PasswordStrength invalid(String message) {
            return new PasswordStrength(false, 0, message);
        }
    }
}
