package com.healthcare.auth.api.dto;

import java.util.List;

public record MfaSetupResponse(
    String secret,
    String qrCodeUri,
    String issuer,
    String accountName
) {
    public static MfaSetupResponse from(
            String secret,
            String qrCodeUri,
            String issuer,
            String accountName
    ) {
        return new MfaSetupResponse(secret, qrCodeUri, issuer, accountName);
    }
}
