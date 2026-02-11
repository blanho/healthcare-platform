package com.healthcare.provider.api.dto;

import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.provider.domain.ProviderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProviderResponse(
    UUID id,
    String providerNumber,
    String firstName,
    String middleName,
    String lastName,
    String fullName,
    String displayName,
    String email,
    String phoneNumber,
    ProviderType providerType,
    String specialization,
    LicenseResponse license,
    String npiNumber,
    String qualification,
    Integer yearsOfExperience,
    BigDecimal consultationFee,
    boolean acceptingPatients,
    ProviderStatus status,
    List<ScheduleResponse> schedules,
    Instant createdAt,
    Instant updatedAt
) {

    public record LicenseResponse(
        String licenseNumber,
        String licenseState,
        LocalDate expiryDate,
        boolean valid,
        long daysUntilExpiry
    ) {}

    public record ScheduleResponse(
        UUID id,
        String dayOfWeek,
        String startTime,
        String endTime,
        int slotDurationMinutes,
        int availableSlotCount,
        boolean active
    ) {}
}
