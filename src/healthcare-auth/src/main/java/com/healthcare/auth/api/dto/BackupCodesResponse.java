package com.healthcare.auth.api.dto;

import java.util.List;

public record BackupCodesResponse(
    List<String> backupCodes,
    String message
) {
    public static BackupCodesResponse of(List<String> codes) {
        return new BackupCodesResponse(
            codes,
            "Store these codes securely. Each code can only be used once."
        );
    }
}
