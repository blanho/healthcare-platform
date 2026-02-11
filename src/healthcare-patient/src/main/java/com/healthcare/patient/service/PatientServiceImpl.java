package com.healthcare.patient.service;

import com.healthcare.common.api.PageResponse;
import com.healthcare.common.config.RedisCacheConfig;
import com.healthcare.patient.api.dto.CreatePatientRequest;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import com.healthcare.patient.api.dto.PatientMapper;
import com.healthcare.patient.api.dto.PatientResponse;
import com.healthcare.patient.api.dto.PatientSearchCriteria;
import com.healthcare.patient.api.dto.PatientSummaryResponse;
import com.healthcare.patient.api.dto.UpdatePatientRequest;
import com.healthcare.patient.domain.Patient;
import com.healthcare.patient.domain.PatientStatus;
import com.healthcare.patient.exception.DuplicatePatientException;
import com.healthcare.patient.exception.PatientNotFoundException;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientDomainMapper domainMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {
        log.debug("Creating new patient");

        validateUniqueEmail(request.email());

        Patient patient = buildPatientFromRequest(request);
        patient = patientRepository.save(patient);

        log.info("Created patient with ID: {} and MRN: {}",
            patient.getId(), patient.getMedicalRecordNumber());

        publishDomainEvents(patient);

        return patientMapper.toResponse(patient);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.CACHE_PATIENTS, key = "#id")
    public Optional<PatientResponse> getPatientById(UUID id) {
        log.debug("Fetching patient by ID: {}", id);
        return patientRepository.findById(id)
            .map(patientMapper::toResponse);
    }

    @Override
    public Optional<PatientResponse> getPatientByMrn(String mrn) {
        log.debug("Fetching patient by MRN: {}", mrn);
        return patientRepository.findByMedicalRecordNumber(mrn)
            .map(patientMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_PATIENTS, key = "#id")
    public PatientResponse updatePatient(UUID id, UpdatePatientRequest request) {
        log.debug("Updating patient with ID: {}", id);

        Patient patient = findPatientOrThrow(id);

        updatePatientFields(patient, request);
        patient = patientRepository.save(patient);

        log.info("Updated patient with ID: {}", patient.getId());

        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_PATIENTS, key = "#id")
    public PatientResponse activatePatient(UUID id) {
        log.debug("Activating patient with ID: {}", id);

        Patient patient = findPatientOrThrow(id);
        patient.activate();
        patient = patientRepository.save(patient);

        publishDomainEvents(patient);
        log.info("Activated patient with ID: {}", patient.getId());

        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_PATIENTS, key = "#id")
    public PatientResponse deactivatePatient(UUID id) {
        log.debug("Deactivating patient with ID: {}", id);

        Patient patient = findPatientOrThrow(id);
        patient.deactivate();
        patient = patientRepository.save(patient);

        publishDomainEvents(patient);
        log.info("Deactivated patient with ID: {}", patient.getId());

        return patientMapper.toResponse(patient);
    }

    @Override
    @Transactional
    @CacheEvict(value = RedisCacheConfig.CACHE_PATIENTS, key = "#id")
    public void deletePatient(UUID id) {
        log.debug("Soft deleting patient with ID: {}", id);

        Patient patient = findPatientOrThrow(id);
        patientRepository.delete(patient);

        log.info("Soft deleted patient with ID: {}", id);
    }

    @Override
    public PageResponse<PatientSummaryResponse> listPatients(Pageable pageable) {
        log.debug("Listing patients with pageable: {}", pageable);

        Page<Patient> page = patientRepository.findAll(pageable);
        Page<PatientSummaryResponse> responsePage = page.map(patientMapper::toSummaryResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    public PageResponse<PatientSummaryResponse> searchPatients(
            PatientSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching patients with criteria: {}", criteria);

        Specification<Patient> spec = buildSearchSpecification(criteria);
        Page<Patient> page = patientRepository.findAll(spec, pageable);
        Page<PatientSummaryResponse> responsePage = page.map(patientMapper::toSummaryResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    public boolean canScheduleAppointments(UUID id) {
        return patientRepository.findById(id)
            .map(Patient::canScheduleAppointments)
            .orElse(false);
    }

    private Patient findPatientOrThrow(UUID id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> PatientNotFoundException.byId(id));
    }

    private void validateUniqueEmail(String email) {
        if (patientRepository.existsByEmail(email)) {
            throw DuplicatePatientException.byEmail(email);
        }
    }

    private Patient buildPatientFromRequest(CreatePatientRequest request) {
        return Patient.builder()
            .firstName(request.firstName())
            .middleName(request.middleName())
            .lastName(request.lastName())
            .dateOfBirth(request.dateOfBirth())
            .gender(request.gender())
            .bloodType(request.bloodType())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .secondaryPhone(request.secondaryPhone())
            .socialSecurityNumber(request.socialSecurityNumber())
            .medicalRecordNumber(generateMedicalRecordNumber())
            .status(PatientStatus.PENDING)
            .address(request.address() != null ? domainMapper.toAddress(request.address()) : null)
            .insurance(request.insurance() != null ? domainMapper.toInsurance(request.insurance()) : null)
            .emergencyContact(request.emergencyContact() != null ?
                domainMapper.toEmergencyContact(request.emergencyContact()) : null)
            .build();
    }

    private void updatePatientFields(Patient patient, UpdatePatientRequest request) {
        if (request.firstName() != null) {
            patient.setFirstName(request.firstName());
        }
        if (request.middleName() != null) {
            patient.setMiddleName(request.middleName());
        }
        if (request.lastName() != null) {
            patient.setLastName(request.lastName());
        }
        if (request.dateOfBirth() != null) {
            patient.setDateOfBirth(request.dateOfBirth());
        }
        if (request.gender() != null) {
            patient.setGender(request.gender());
        }
        if (request.bloodType() != null) {
            patient.setBloodType(request.bloodType());
        }
        if (request.email() != null) {
            patient.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            patient.setPhoneNumber(request.phoneNumber());
        }
        if (request.secondaryPhone() != null) {
            patient.setSecondaryPhone(request.secondaryPhone());
        }
        if (request.address() != null) {
            patient.setAddress(domainMapper.toAddress(request.address()));
        }
        if (request.insurance() != null) {
            patient.setInsurance(domainMapper.toInsurance(request.insurance()));
        }
        if (request.emergencyContact() != null) {
            patient.setEmergencyContact(domainMapper.toEmergencyContact(request.emergencyContact()));
        }
    }

    private String generateMedicalRecordNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "MRN-" + datePart + "-" + uniquePart;
    }

    private Specification<Patient> buildSearchSpecification(PatientSearchCriteria criteria) {
        return Specification.where(
            hasName(criteria.name()))
            .and(hasEmail(criteria.email()))
            .and(hasPhoneNumber(criteria.phoneNumber()))
            .and(hasMedicalRecordNumber(criteria.medicalRecordNumber()))
            .and(hasStatus(criteria.status())
        );
    }

    private Specification<Patient> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return null;
            }
            String pattern = "%" + name.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern)
            );
        };
    }

    private Specification<Patient> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    private Specification<Patient> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) -> {
            if (phoneNumber == null || phoneNumber.isBlank()) {
                return null;
            }
            return cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%");
        };
    }

    private Specification<Patient> hasMedicalRecordNumber(String mrn) {
        return (root, query, cb) -> {
            if (mrn == null || mrn.isBlank()) {
                return null;
            }
            return cb.equal(root.get("medicalRecordNumber"), mrn);
        };
    }

    private Specification<Patient> hasStatus(PatientStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            return cb.equal(root.get("status"), status);
        };
    }

    private void publishDomainEvents(Patient patient) {
        patient.getDomainEvents().forEach(eventPublisher::publishEvent);
        patient.clearDomainEvents();
    }

    @Override
    public long count() {
        return patientRepository.count();
    }

    @Override
    public long countByStatus(PatientStatus status) {
        return patientRepository.countByStatus(status);
    }

    @Override
    public long countCreatedBefore(java.time.Instant instant) {
        return patientRepository.countByCreatedAtBefore(instant);
    }

    @Override
    public Optional<PatientResponse> findById(UUID id) {
        return getPatientById(id);
    }
}
