package com.healthcare.provider.api.dto;

import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.provider.domain.ProviderType;

public record ProviderSearchCriteria(
    String name,
    String email,
    ProviderType providerType,
    String specialization,
    ProviderStatus status,
    Boolean acceptingPatients
) {}
