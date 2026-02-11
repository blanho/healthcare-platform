package com.healthcare.auth.api;

import com.healthcare.auth.api.dto.BackupCodesResponse;
import com.healthcare.auth.api.dto.MfaDisableRequest;
import com.healthcare.auth.api.dto.MfaSetupRequest;
import com.healthcare.auth.api.dto.MfaSetupResponse;
import com.healthcare.auth.api.dto.MfaVerifyRequest;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.service.MfaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/mfa")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MFA", description = "Multi-Factor Authentication operations")
public class MfaController {

    private final MfaService mfaService;

    @GetMapping("/setup")
    @Operation(summary = "Generate MFA setup", description = "Generate secret and QR code for MFA setup")
    public ResponseEntity<MfaSetupResponse> generateSetup(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        log.debug("Generating MFA setup for user: {}", user.getId());

        MfaService.MfaSetupData setupData = mfaService.generateMfaSetup(user.getId());

        return ResponseEntity.ok(MfaSetupResponse.from(
            setupData.secret(),
            setupData.qrCodeUri(),
            setupData.issuer(),
            setupData.accountName()
        ));
    }

    @PostMapping("/enable")
    @Operation(summary = "Enable MFA", description = "Enable MFA after verifying setup code")
    public ResponseEntity<BackupCodesResponse> enableMfa(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MfaSetupRequest request
    ) {
        log.debug("Enabling MFA for user: {}", user.getId());

        List<String> backupCodes = mfaService.enableMfa(
            user.getId(),
            request.secret(),
            request.code()
        );

        return ResponseEntity.ok(BackupCodesResponse.of(backupCodes));
    }

    @DeleteMapping("/disable")
    @Operation(summary = "Disable MFA", description = "Disable MFA (requires password)")
    public ResponseEntity<Void> disableMfa(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MfaDisableRequest request
    ) {
        log.debug("Disabling MFA for user: {}", user.getId());

        mfaService.disableMfa(user.getId(), request.password());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify MFA code", description = "Verify TOTP or backup code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MfaVerifyRequest request,
            HttpServletRequest httpRequest
    ) {
        log.debug("Verifying MFA code for user: {}", user.getId());

        boolean valid;
        if (request.useBackupCode()) {
            String ipAddress = getClientIpAddress(httpRequest);
            valid = mfaService.verifyBackupCode(user.getId(), request.code(), ipAddress);
        } else {
            valid = mfaService.verifyTotpCode(user.getId(), request.code());
        }

        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/backup-codes/regenerate")
    @Operation(summary = "Regenerate backup codes", description = "Generate new backup codes (requires password)")
    public ResponseEntity<BackupCodesResponse> regenerateBackupCodes(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MfaDisableRequest request
    ) {
        log.debug("Regenerating backup codes for user: {}", user.getId());

        List<String> backupCodes = mfaService.regenerateBackupCodes(user.getId(), request.password());

        return ResponseEntity.ok(BackupCodesResponse.of(backupCodes));
    }

    @GetMapping("/backup-codes/count")
    @Operation(summary = "Get remaining backup codes", description = "Get count of unused backup codes")
    public ResponseEntity<Map<String, Integer>> getBackupCodeCount(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        int count = mfaService.getRemainingBackupCodeCount(user.getId());

        return ResponseEntity.ok(Map.of("remaining", count));
    }

    @GetMapping("/status")
    @Operation(summary = "Get MFA status", description = "Check if MFA is enabled")
    public ResponseEntity<Map<String, Object>> getMfaStatus(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        boolean enabled = mfaService.isMfaEnabled(user.getId());
        int backupCodes = enabled ? mfaService.getRemainingBackupCodeCount(user.getId()) : 0;

        return ResponseEntity.ok(Map.of(
            "enabled", enabled,
            "backupCodesRemaining", backupCodes
        ));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
