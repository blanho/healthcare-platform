package com.healthcare.provider.service;

import com.healthcare.common.api.PageResponse;
import com.healthcare.common.config.RedisCacheConfig;
import com.healthcare.provider.api.dto.CreateProviderRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.healthcare.provider.api.dto.ProviderResponse;
import com.healthcare.provider.api.dto.ProviderSearchCriteria;
import com.healthcare.provider.api.dto.ProviderSummaryResponse;
import com.healthcare.provider.api.dto.ScheduleRequest;
import com.healthcare.provider.api.dto.UpdateProviderRequest;
import com.healthcare.provider.domain.MedicalLicense;
import com.healthcare.provider.domain.Provider;
import com.healthcare.provider.domain.ProviderSchedule;
import com.healthcare.provider.exception.DuplicateProviderException;
import com.healthcare.provider.exception.ProviderNotFoundException;
import com.healthcare.provider.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderNumberGenerator providerNumberGenerator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ProviderResponse createProvider(CreateProviderRequest request) {
        log.info("Creating provider");

        if (providerRepository.existsByEmail(request.email())) {
            throw DuplicateProviderException.byEmail(request.email());
        }
        if (providerRepository.existsByLicenseNumber(request.license().licenseNumber())) {
            throw DuplicateProviderException.byLicenseNumber(request.license().licenseNumber());
        }

        String providerNumber = providerNumberGenerator.generateProviderNumber(request.providerType());

        MedicalLicense license = MedicalLicense.builder()
            .licenseNumber(request.license().licenseNumber())
            .licenseState(request.license().licenseState())
            .expiryDate(request.license().expiryDate())
            .build();

        Provider provider = Provider.builder()
            .providerNumber(providerNumber)
            .firstName(request.firstName())
            .middleName(request.middleName())
            .lastName(request.lastName())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .providerType(request.providerType())
            .specialization(request.specialization())
            .license(license)
            .npiNumber(request.npiNumber())
            .qualification(request.qualification())
            .yearsOfExperience(request.yearsOfExperience())
            .consultationFee(request.consultationFee())
            .acceptingPatients(request.acceptingPatients() != null ? request.acceptingPatients() : true)
            .build();

        Provider saved = providerRepository.save(provider);
        log.info("Created provider with ID: {} and number: {}", saved.getId(), saved.getProviderNumber());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = RedisCacheConfig.CACHE_PROVIDERS, key = "#id")
    public Optional<ProviderResponse> getProviderById(UUID id) {
        log.debug("Getting provider by ID: {}", id);
        return providerRepository.findByIdWithSchedules(id)
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProviderResponse> getProviderByProviderNumber(String providerNumber) {
        log.debug("Getting provider by provider number: {}", providerNumber);
        return providerRepository.findByProviderNumber(providerNumber)
            .map(this::toResponse);
    }

    @Override
    @CacheEvict(value = RedisCacheConfig.CACHE_PROVIDERS, key = "#id")
    public ProviderResponse updateProvider(UUID id, UpdateProviderRequest request) {
        log.info("Updating provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        if (request.email() != null && !request.email().equals(provider.getEmail())) {
            if (providerRepository.existsByEmail(request.email())) {
                throw DuplicateProviderException.byEmail(request.email());
            }
        }

        if (request.firstName() != null) provider.setFirstName(request.firstName());
        if (request.middleName() != null) provider.setMiddleName(request.middleName());
        if (request.lastName() != null) provider.setLastName(request.lastName());
        if (request.email() != null) provider.setEmail(request.email());
        if (request.phoneNumber() != null) provider.setPhoneNumber(request.phoneNumber());
        if (request.providerType() != null) provider.setProviderType(request.providerType());
        if (request.specialization() != null) provider.setSpecialization(request.specialization());
        if (request.npiNumber() != null) provider.setNpiNumber(request.npiNumber());
        if (request.qualification() != null) provider.setQualification(request.qualification());
        if (request.yearsOfExperience() != null) provider.setYearsOfExperience(request.yearsOfExperience());
        if (request.consultationFee() != null) provider.setConsultationFee(request.consultationFee());

        if (request.acceptingPatients() != null) {
            if (request.acceptingPatients()) {
                provider.startAcceptingPatients();
            } else {
                provider.stopAcceptingPatients();
            }
        }

        if (request.license() != null) {
            MedicalLicense currentLicense = provider.getLicense();
            MedicalLicense updatedLicense = MedicalLicense.builder()
                .licenseNumber(request.license().licenseNumber() != null ?
                    request.license().licenseNumber() : currentLicense.getLicenseNumber())
                .licenseState(request.license().licenseState() != null ?
                    request.license().licenseState() : currentLicense.getLicenseState())
                .expiryDate(request.license().expiryDate() != null ?
                    request.license().expiryDate() : currentLicense.getExpiryDate())
                .build();
            provider.updateLicense(updatedLicense);
        }

        Provider saved = providerRepository.save(provider);
        log.info("Updated provider with ID: {}", id);

        return toResponse(saved);
    }

    @Override
    public void deleteProvider(UUID id) {
        log.info("Deleting provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.terminate();
        providerRepository.save(provider);

        log.info("Soft deleted provider with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderSummaryResponse> listProviders(Pageable pageable) {
        log.debug("Listing providers with pageable: {}", pageable);

        Page<Provider> page = providerRepository.findByDeletedFalse(pageable);
        Page<ProviderSummaryResponse> responsePage = page.map(this::toSummary);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderSummaryResponse> searchProviders(ProviderSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching providers with criteria: {}", criteria);

        Specification<Provider> spec = buildSpecification(criteria);
        Page<Provider> page = providerRepository.findAll(spec, pageable);
        Page<ProviderSummaryResponse> responsePage = page.map(this::toSummary);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderSummaryResponse> listAcceptingPatients(Pageable pageable) {
        log.debug("Listing providers accepting patients");

        Page<Provider> page = providerRepository.findAcceptingPatients(pageable);
        Page<ProviderSummaryResponse> responsePage = page.map(this::toSummary);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllSpecializations() {
        return providerRepository.findAllSpecializations();
    }

    @Override
    public ProviderResponse activateProvider(UUID id) {
        log.info("Activating provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.activate();
        Provider saved = providerRepository.save(provider);

        saved.getDomainEvents().forEach(eventPublisher::publishEvent);
        saved.clearDomainEvents();

        return toResponse(saved);
    }

    @Override
    public ProviderResponse deactivateProvider(UUID id) {
        log.info("Deactivating provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.deactivate();
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse putProviderOnLeave(UUID id) {
        log.info("Putting provider on leave with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.putOnLeave();
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse returnProviderFromLeave(UUID id) {
        log.info("Returning provider from leave with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.returnFromLeave();
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse suspendProvider(UUID id) {
        log.info("Suspending provider with ID: {}", id);

        Provider provider = providerRepository.findById(id)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));

        provider.suspend();
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse addSchedule(UUID providerId, ScheduleRequest request) {
        log.info("Adding schedule to provider: {} for {}", providerId, request.dayOfWeek());

        Provider provider = providerRepository.findByIdWithSchedules(providerId)
            .orElseThrow(() -> ProviderNotFoundException.byId(providerId));

        ProviderSchedule schedule = ProviderSchedule.builder()
            .dayOfWeek(request.dayOfWeek())
            .startTime(request.startTime())
            .endTime(request.endTime())
            .slotDurationMinutes(request.slotDurationMinutes())
            .active(true)
            .build();

        provider.addSchedule(schedule);
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse updateSchedule(UUID providerId, UUID scheduleId, ScheduleRequest request) {
        log.info("Updating schedule {} for provider: {}", scheduleId, providerId);

        Provider provider = providerRepository.findByIdWithSchedules(providerId)
            .orElseThrow(() -> ProviderNotFoundException.byId(providerId));

        ProviderSchedule schedule = provider.getSchedules().stream()
            .filter(s -> s.getId().equals(scheduleId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        schedule.updateTimeSlot(request.startTime(), request.endTime());
        schedule.setSlotDurationMinutes(request.slotDurationMinutes());

        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    public ProviderResponse removeSchedule(UUID providerId, UUID scheduleId) {
        log.info("Removing schedule {} from provider: {}", scheduleId, providerId);

        Provider provider = providerRepository.findByIdWithSchedules(providerId)
            .orElseThrow(() -> ProviderNotFoundException.byId(providerId));

        ProviderSchedule schedule = provider.getSchedules().stream()
            .filter(s -> s.getId().equals(scheduleId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));

        provider.removeSchedule(schedule);
        Provider saved = providerRepository.save(provider);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canBeScheduled(UUID id) {
        return providerRepository.findById(id)
            .map(Provider::canBeScheduled)
            .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return providerRepository.existsById(id);
    }

    private ProviderResponse toResponse(Provider provider) {
        return new ProviderResponse(
            provider.getId(),
            provider.getProviderNumber(),
            provider.getFirstName(),
            provider.getMiddleName(),
            provider.getLastName(),
            provider.getFullName(),
            provider.getDisplayName(),
            provider.getEmail(),
            provider.getPhoneNumber(),
            provider.getProviderType(),
            provider.getSpecialization(),
            toLicenseResponse(provider.getLicense()),
            provider.getNpiNumber(),
            provider.getQualification(),
            provider.getYearsOfExperience(),
            provider.getConsultationFee(),
            provider.isAcceptingPatients(),
            provider.getStatus(),
            provider.getSchedules().stream()
                .map(this::toScheduleResponse)
                .toList(),
            provider.getCreatedAt(),
            provider.getUpdatedAt()
        );
    }

    private ProviderResponse.LicenseResponse toLicenseResponse(MedicalLicense license) {
        return new ProviderResponse.LicenseResponse(
            license.getLicenseNumber(),
            license.getLicenseState(),
            license.getExpiryDate(),
            license.isValid(),
            license.daysUntilExpiry()
        );
    }

    private ProviderResponse.ScheduleResponse toScheduleResponse(ProviderSchedule schedule) {
        return new ProviderResponse.ScheduleResponse(
            schedule.getId(),
            schedule.getDayOfWeek().name(),
            schedule.getStartTime().toString(),
            schedule.getEndTime().toString(),
            schedule.getSlotDurationMinutes(),
            schedule.getAvailableSlotCount(),
            schedule.isActive()
        );
    }

    private ProviderSummaryResponse toSummary(Provider provider) {
        return new ProviderSummaryResponse(
            provider.getId(),
            provider.getProviderNumber(),
            provider.getDisplayName(),
            provider.getEmail(),
            provider.getProviderType(),
            provider.getSpecialization(),
            provider.isAcceptingPatients(),
            provider.getStatus()
        );
    }

    private Specification<Provider> buildSpecification(ProviderSearchCriteria criteria) {
        return Specification.where(notDeleted())
            .and(hasName(criteria.name()))
            .and(hasEmail(criteria.email()))
            .and(hasProviderType(criteria.providerType()))
            .and(hasSpecialization(criteria.specialization()))
            .and(hasStatus(criteria.status()))
            .and(isAcceptingPatients(criteria.acceptingPatients()));
    }

    private Specification<Provider> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private Specification<Provider> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }

    private Specification<Provider> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    private Specification<Provider> hasProviderType(com.healthcare.provider.domain.ProviderType type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("providerType"), type);
        };
    }

    private Specification<Provider> hasSpecialization(String specialization) {
        return (root, query, cb) -> {
            if (specialization == null || specialization.isBlank()) return null;
            return cb.like(cb.lower(root.get("specialization")), "%" + specialization.toLowerCase() + "%");
        };
    }

    private Specification<Provider> hasStatus(com.healthcare.provider.domain.ProviderStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    private Specification<Provider> isAcceptingPatients(Boolean accepting) {
        return (root, query, cb) -> {
            if (accepting == null) return null;
            return cb.equal(root.get("acceptingPatients"), accepting);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return providerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(com.healthcare.provider.domain.ProviderStatus status) {
        return providerRepository.countByStatusAndDeletedFalse(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProviderResponse> findById(UUID id) {
        return getProviderById(id);
    }
}
