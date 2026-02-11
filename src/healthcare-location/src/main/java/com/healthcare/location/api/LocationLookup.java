package com.healthcare.location.api;

import com.healthcare.location.domain.LocationType;

import java.util.Optional;
import java.util.UUID;

public interface LocationLookup {

    Optional<LocationInfo> findById(UUID locationId);

    Optional<LocationInfo> findByLocationCode(String locationCode);

    record LocationInfo(
        UUID locationId,
        String locationCode,
        String name,
        LocationType type,
        String fullAddress,
        String phoneNumber,
        boolean active
    ) {}
}
