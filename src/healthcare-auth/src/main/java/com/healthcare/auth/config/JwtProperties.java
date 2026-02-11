package com.healthcare.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "healthcare.security.jwt")
@Validated
public record JwtProperties(
    @NotBlank(message = "JWT secret is required")
    String secret,

    @Min(value = 60, message = "Access token expiration must be at least 60 seconds")
    long accessTokenExpiration,

    @Min(value = 3600, message = "Refresh token expiration must be at least 1 hour")
    long refreshTokenExpiration,

    @NotBlank(message = "JWT issuer is required")
    String issuer
) {

    private static final String DEV_SECRET_MARKER = "do-not-use-in-production";

    public JwtProperties {

        String activeProfiles = System.getProperty("spring.profiles.active",
            System.getenv().getOrDefault("SPRING_PROFILES_ACTIVE", ""));

        if (secret != null && secret.toLowerCase().contains(DEV_SECRET_MARKER)
                && activeProfiles.contains("prod")) {
            throw new IllegalStateException(
                "Production JWT secret not configured. Set JWT_SECRET environment variable. " +
                "The development fallback secret cannot be used in production.");
        }

        if (accessTokenExpiration == 0) {
            accessTokenExpiration = 900;
        }
        if (refreshTokenExpiration == 0) {
            refreshTokenExpiration = 604800;
        }
        if (issuer == null || issuer.isBlank()) {
            issuer = "healthcare-platform";
        }
    }
}
