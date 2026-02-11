package com.healthcare.provider.api;

import java.util.Optional;
import java.util.UUID;

public interface ProviderLookup {

    Optional<ProviderInfo> findById(UUID providerId);

    record ProviderInfo(
        UUID providerId,
        String providerNumber,
        String fullName,
        String specialization,
        String qualification
    ) {}
}
