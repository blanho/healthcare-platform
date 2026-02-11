package com.healthcare.billing.api;

import java.util.Optional;
import java.util.UUID;

public interface ClaimLookup {

    Optional<ClaimInfo> findById(UUID claimId);

    Optional<ClaimInfo> findByClaimNumber(String claimNumber);

    record ClaimInfo(
        UUID claimId,
        String claimNumber,
        UUID patientId,
        UUID providerId,
        String insuranceName,
        String status
    ) {}
}
