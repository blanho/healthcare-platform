package com.healthcare.provider.repository;

import com.healthcare.provider.domain.Provider;
import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.provider.domain.ProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID>, JpaSpecificationExecutor<Provider> {

    Optional<Provider> findByProviderNumber(String providerNumber);

    Optional<Provider> findByEmail(String email);

    @Query("SELECT p FROM Provider p WHERE p.license.licenseNumber = :licenseNumber")
    Optional<Provider> findByLicenseNumber(@Param("licenseNumber") String licenseNumber);

    Optional<Provider> findByNpiNumber(String npiNumber);

    boolean existsByProviderNumber(String providerNumber);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Provider p WHERE p.license.licenseNumber = :licenseNumber")
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);

    Page<Provider> findByStatusAndDeletedFalse(ProviderStatus status, Pageable pageable);

    Page<Provider> findByDeletedFalse(Pageable pageable);

    Page<Provider> findByProviderTypeAndDeletedFalse(ProviderType type, Pageable pageable);

    Page<Provider> findBySpecializationIgnoreCaseAndDeletedFalse(String specialization, Pageable pageable);

    @Query("SELECT p FROM Provider p WHERE p.acceptingPatients = true AND p.status = 'ACTIVE' AND p.deleted = false")
    Page<Provider> findAcceptingPatients(Pageable pageable);

    @EntityGraph(attributePaths = {"schedules"})
    @Query("SELECT p FROM Provider p WHERE p.id = :id AND p.deleted = false")
    Optional<Provider> findByIdWithSchedules(@Param("id") UUID id);

    @Query("SELECT p FROM Provider p WHERE p.license.expiryDate <= :expiryDate AND p.deleted = false")
    List<Provider> findWithExpiringLicenses(@Param("expiryDate") LocalDate expiryDate);

    @Query("SELECT p FROM Provider p WHERE p.deleted = false AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Provider> searchByName(@Param("name") String name, Pageable pageable);

    long countByStatusAndDeletedFalse(ProviderStatus status);

    long countByProviderTypeAndDeletedFalse(ProviderType type);

    @Query("SELECT DISTINCT p.specialization FROM Provider p WHERE p.specialization IS NOT NULL AND p.deleted = false ORDER BY p.specialization")
    List<String> findAllSpecializations();
}
