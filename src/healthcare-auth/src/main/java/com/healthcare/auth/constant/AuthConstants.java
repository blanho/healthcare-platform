package com.healthcare.auth.constant;

public final class AuthConstants {

    private AuthConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    public static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 15;
    public static final int REFRESH_TOKEN_EXPIRATION_DAYS = 30;
    public static final String TOKEN_TYPE = "Bearer";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 128;
    public static final int PASSWORD_HISTORY_SIZE = 5;

    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCKOUT_MINUTES = 30;

    public static final int SESSION_TIMEOUT_MINUTES = 30;
    public static final int REMEMBER_ME_DAYS = 14;
}
