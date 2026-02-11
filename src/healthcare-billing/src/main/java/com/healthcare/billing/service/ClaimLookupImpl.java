package com.healthcare.billing.service;

import com.healthcare.billing.api.ClaimLookup;
import com.healthcare.billing.domain.Claim;
import com.healthcare.billing.repository.ClaimRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class ClaimLookupImpl implements ClaimLookup {

    private final ClaimRepository claimRepository;

    ClaimLookupImpl(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @Override
    public Optional<ClaimInfo> findById(UUID claimId) {
        return claimRepository.findById(claimId)
            .map(this::toClaimInfo);
    }

    @Override
    public Optional<ClaimInfo> findByClaimNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber)
            .map(this::toClaimInfo);
    }

    private ClaimInfo toClaimInfo(Claim claim) {
        return new ClaimInfo(
            claim.getId(),
            claim.getClaimNumber(),
            claim.getPatientId(),
            claim.getProviderId(),
            claim.getInsuranceName(),
            claim.getStatus().name()
        );
    }
}
