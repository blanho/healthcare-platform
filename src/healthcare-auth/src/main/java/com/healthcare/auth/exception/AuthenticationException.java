package com.healthcare.auth.exception;

import com.healthcare.common.exception.HealthcareException;

public class AuthenticationException extends HealthcareException {

    private static final String ERROR_CODE = "AUTHENTICATION_FAILED";

    public AuthenticationException(String message) {
        super(message, ERROR_CODE);
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid username or password");
    }

    public static AuthenticationException accountLocked() {
        return new AuthenticationException("Account is locked due to too many failed login attempts");
    }

    public static AuthenticationException accountInactive() {
        return new AuthenticationException("Account is inactive or suspended");
    }

    public static AuthenticationException emailNotVerified() {
        return new AuthenticationException("Email address has not been verified");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("Authentication token has expired");
    }

    public static AuthenticationException tokenInvalid() {
        return new AuthenticationException("Authentication token is invalid");
    }

    public static AuthenticationException refreshTokenInvalid() {
        return new AuthenticationException("Refresh token is invalid or expired");
    }

    public static AuthenticationException mfaRequired() {
        return new AuthenticationException("Multi-factor authentication is required");
    }

    public static AuthenticationException mfaCodeInvalid() {
        return new AuthenticationException("Invalid MFA code");
    }

    public static AuthenticationException mfaAlreadyEnabled() {
        return new AuthenticationException("MFA is already enabled for this account");
    }

    public static AuthenticationException mfaNotEnabled() {
        return new AuthenticationException("MFA is not enabled for this account");
    }

    public static AuthenticationException passwordResetTokenInvalid() {
        return new AuthenticationException("Password reset token is invalid or expired");
    }

    public static AuthenticationException passwordResetRateLimited() {
        return new AuthenticationException("Too many password reset requests. Please try again later");
    }

    public static AuthenticationException passwordReused() {
        return new AuthenticationException("New password cannot be the same as any of your recent passwords");
    }

    public static AuthenticationException passwordChangeRequired() {
        return new AuthenticationException("Password change is required");
    }

    public static AuthenticationException emailVerificationTokenInvalid() {
        return new AuthenticationException("Email verification token is invalid or expired");
    }

    public static AuthenticationException emailAlreadyVerified() {
        return new AuthenticationException("Email address is already verified");
    }

    public static AuthenticationException tooManyAttempts() {
        return new AuthenticationException("Too many login attempts. Please try again later");
    }

    public static AuthenticationException captchaRequired() {
        return new AuthenticationException("CAPTCHA verification is required");
    }

    public static AuthenticationException captchaInvalid() {
        return new AuthenticationException("CAPTCHA verification failed");
    }
}
