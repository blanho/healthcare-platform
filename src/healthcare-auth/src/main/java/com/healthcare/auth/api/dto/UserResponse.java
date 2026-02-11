package com.healthcare.auth.api.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String username,
    String email,
    String firstName,
    String lastName,
    String fullName,
    String phoneNumber,
    String status,
    boolean emailVerified,
    boolean mfaEnabled,
    UUID patientId,
    UUID providerId,
    Set<String> roles,
    Set<String> permissions,
    Instant lastLoginAt,
    Instant createdAt,
    Instant updatedAt
) {}
