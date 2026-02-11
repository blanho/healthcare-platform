package com.healthcare.provider.api.dto;

import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.provider.domain.ProviderType;

import java.util.UUID;

public record ProviderSummaryResponse(
    UUID id,
    String providerNumber,
    String displayName,
    String email,
    ProviderType providerType,
    String specialization,
    boolean acceptingPatients,
    ProviderStatus status
) {}
