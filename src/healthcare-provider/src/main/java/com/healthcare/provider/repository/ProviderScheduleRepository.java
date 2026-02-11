package com.healthcare.provider.repository;

import com.healthcare.provider.domain.ProviderSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderScheduleRepository extends JpaRepository<ProviderSchedule, UUID> {

    List<ProviderSchedule> findByProviderId(UUID providerId);

    List<ProviderSchedule> findByProviderIdAndActiveTrue(UUID providerId);

    Optional<ProviderSchedule> findByProviderIdAndDayOfWeek(UUID providerId, DayOfWeek dayOfWeek);

    @Query("SELECT ps.provider.id FROM ProviderSchedule ps WHERE ps.dayOfWeek = :dayOfWeek AND ps.active = true")
    List<UUID> findProviderIdsAvailableOnDay(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    void deleteByProviderId(UUID providerId);

    long countByProviderIdAndActiveTrue(UUID providerId);
}
