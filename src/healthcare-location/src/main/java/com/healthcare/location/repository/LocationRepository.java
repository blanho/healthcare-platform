package com.healthcare.location.repository;

import com.healthcare.location.domain.Location;
import com.healthcare.location.domain.LocationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByLocationCode(String locationCode);

    Page<Location> findByIsActiveTrueOrderByName(Pageable pageable);

    Page<Location> findByTypeAndIsActiveTrueOrderByName(LocationType type, Pageable pageable);

    boolean existsByLocationCode(String locationCode);
}
