package com.healthcare.auth.service;

import java.util.List;
import java.util.UUID;

public interface MfaService {

    MfaSetupData generateMfaSetup(UUID userId);

    List<String> enableMfa(UUID userId, String secret, String code);

    void disableMfa(UUID userId, String password);

    boolean verifyTotpCode(UUID userId, String code);

    boolean verifyBackupCode(UUID userId, String code, String ipAddress);

    List<String> regenerateBackupCodes(UUID userId, String password);

    int getRemainingBackupCodeCount(UUID userId);

    boolean isMfaEnabled(UUID userId);

    record MfaSetupData(
        String secret,
        String qrCodeUri,
        String issuer,
        String accountName
    ) {}
}
