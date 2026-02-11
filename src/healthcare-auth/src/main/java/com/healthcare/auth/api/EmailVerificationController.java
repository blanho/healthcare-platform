package com.healthcare.auth.api;

import com.healthcare.auth.api.dto.VerifyEmailRequest;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Email Verification", description = "Email verification operations")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/verify")
    @Operation(summary = "Verify email", description = "Verify email address using token")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        log.debug("Verifying email with token");

        emailVerificationService.verifyEmail(request.token());

        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/resend")
    @Operation(summary = "Resend verification email", description = "Resend verification email to user")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        log.debug("Resending verification email for user: {}", user.getId());

        emailVerificationService.resendVerificationEmail(user.getId());

        return ResponseEntity.ok(Map.of("message", "Verification email has been sent"));
    }

    @GetMapping("/status")
    @Operation(summary = "Check email verification status", description = "Check if email is verified")
    public ResponseEntity<Map<String, Boolean>> getVerificationStatus(
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        boolean verified = emailVerificationService.isEmailVerified(user.getId());

        return ResponseEntity.ok(Map.of("verified", verified));
    }
}
