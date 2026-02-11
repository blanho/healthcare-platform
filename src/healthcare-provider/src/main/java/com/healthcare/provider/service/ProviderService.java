package com.healthcare.provider.service;

import com.healthcare.common.api.PageResponse;
import com.healthcare.provider.api.dto.CreateProviderRequest;
import com.healthcare.provider.api.dto.ProviderResponse;
import com.healthcare.provider.api.dto.ProviderSearchCriteria;
import com.healthcare.provider.api.dto.ProviderSummaryResponse;
import com.healthcare.provider.api.dto.ScheduleRequest;
import com.healthcare.provider.api.dto.UpdateProviderRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderService {

    ProviderResponse createProvider(CreateProviderRequest request);

    Optional<ProviderResponse> getProviderById(UUID id);

    Optional<ProviderResponse> getProviderByProviderNumber(String providerNumber);

    ProviderResponse updateProvider(UUID id, UpdateProviderRequest request);

    void deleteProvider(UUID id);

    PageResponse<ProviderSummaryResponse> listProviders(Pageable pageable);

    PageResponse<ProviderSummaryResponse> searchProviders(ProviderSearchCriteria criteria, Pageable pageable);

    PageResponse<ProviderSummaryResponse> listAcceptingPatients(Pageable pageable);

    List<String> getAllSpecializations();

    ProviderResponse activateProvider(UUID id);

    ProviderResponse deactivateProvider(UUID id);

    ProviderResponse putProviderOnLeave(UUID id);

    ProviderResponse returnProviderFromLeave(UUID id);

    ProviderResponse suspendProvider(UUID id);

    ProviderResponse addSchedule(UUID providerId, ScheduleRequest request);

    ProviderResponse updateSchedule(UUID providerId, UUID scheduleId, ScheduleRequest request);

    ProviderResponse removeSchedule(UUID providerId, UUID scheduleId);

    boolean canBeScheduled(UUID id);

    boolean existsById(UUID id);

    long count();

    long countByStatus(com.healthcare.provider.domain.ProviderStatus status);

    Optional<ProviderResponse> findById(UUID id);
}
