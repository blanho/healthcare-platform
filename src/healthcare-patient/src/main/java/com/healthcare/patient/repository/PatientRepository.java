package com.healthcare.patient.repository;

import com.healthcare.patient.domain.Patient;
import com.healthcare.patient.domain.PatientStatus;
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
public interface PatientRepository extends JpaRepository<Patient, UUID>, JpaSpecificationExecutor<Patient> {

    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByMedicalRecordNumber(String medicalRecordNumber);

    Page<Patient> findByStatus(PatientStatus status, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<Patient> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName, Pageable pageable);

    @Query("""
        SELECT p FROM Patient p
        WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
           OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
        """)
    Page<Patient> searchByName(@Param("name") String name, Pageable pageable);

    @Query("""
        SELECT p FROM Patient p
        WHERE p.status = 'ACTIVE'
          AND p.insurance.expirationDate BETWEEN :startDate AND :endDate
        """)
    List<Patient> findPatientsWithExpiringInsurance(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    Page<Patient> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    long countByStatus(PatientStatus status);

    @Query("SELECT p FROM Patient p WHERE p.status = 'ACTIVE'")
    Page<Patient> findAllActive(Pageable pageable);

    long countByCreatedAtBefore(java.time.Instant instant);
}
