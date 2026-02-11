package com.healthcare.auth.api;

import com.healthcare.auth.api.dto.ChangePasswordRequest;
import com.healthcare.auth.api.dto.ForgotPasswordRequest;
import com.healthcare.auth.api.dto.ResetPasswordRequest;
import com.healthcare.auth.api.dto.VerifyEmailRequest;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Password", description = "Password management operations")
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot")
    @Operation(summary = "Forgot password", description = "Request password reset email")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        log.debug("Forgot password request received");

        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        passwordService.forgotPassword(request.email(), ipAddress, userAgent);

        return ResponseEntity.ok(Map.of(
            "message", "If an account exists with this email, a password reset link will be sent"
        ));
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset password", description = "Reset password using token")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        log.debug("Reset password request received");

        passwordService.resetPassword(request.token(), request.newPassword());

        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
    }

    @PostMapping("/change")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.debug("Change password request for user: {}", user.getId());

        passwordService.changePassword(
            user.getId(),
            request.currentPassword(),
            request.newPassword()
        );

        return ResponseEntity.ok(Map.of("message", "Password has been changed successfully"));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate password strength", description = "Check password strength")
    public ResponseEntity<PasswordService.PasswordStrength> validatePasswordStrength(
            @RequestBody Map<String, String> request
    ) {
        String password = request.get("password");
        PasswordService.PasswordStrength strength = passwordService.validatePasswordStrength(password);

        return ResponseEntity.ok(strength);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
