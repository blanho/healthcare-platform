package com.healthcare.auth.service;

import java.util.UUID;

public interface EmailVerificationService {

    void sendVerificationEmail(UUID userId);

    void resendVerificationEmail(UUID userId);

    void verifyEmail(String token);

    boolean isEmailVerified(UUID userId);
}
