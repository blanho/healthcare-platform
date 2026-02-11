package com.healthcare.location.service;

import com.healthcare.location.api.LocationLookup;
import com.healthcare.location.domain.Location;
import com.healthcare.location.repository.LocationRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class LocationLookupImpl implements LocationLookup {

    private final LocationRepository locationRepository;

    LocationLookupImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Optional<LocationInfo> findById(UUID locationId) {
        return locationRepository.findById(locationId)
            .map(this::toLocationInfo);
    }

    @Override
    public Optional<LocationInfo> findByLocationCode(String locationCode) {
        return locationRepository.findByLocationCode(locationCode)
            .map(this::toLocationInfo);
    }

    private LocationInfo toLocationInfo(Location location) {
        return new LocationInfo(
            location.getId(),
            location.getLocationCode(),
            location.getName(),
            location.getType(),
            location.getAddress() != null ? location.getAddress().formatFull() : "",
            location.getPhoneNumber(),
            location.getIsActive()
        );
    }
}
