package com.healthcare.provider.service;

import com.healthcare.provider.api.ProviderLookup;
import com.healthcare.provider.domain.Provider;
import com.healthcare.provider.repository.ProviderRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class ProviderLookupImpl implements ProviderLookup {

    private final ProviderRepository providerRepository;

    ProviderLookupImpl(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Override
    public Optional<ProviderInfo> findById(UUID providerId) {
        return providerRepository.findById(providerId)
            .map(this::toProviderInfo);
    }

    private ProviderInfo toProviderInfo(Provider provider) {
        String fullName = String.format("Dr. %s %s",
            provider.getFirstName(),
            provider.getLastName());

        return new ProviderInfo(
            provider.getId(),
            provider.getProviderNumber(),
            fullName,
            provider.getSpecialization(),
            provider.getQualification()
        );
    }
}
