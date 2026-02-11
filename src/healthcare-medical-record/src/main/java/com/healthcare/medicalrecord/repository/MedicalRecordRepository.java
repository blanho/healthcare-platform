package com.healthcare.medicalrecord.repository;

import com.healthcare.medicalrecord.domain.MedicalRecord;
import com.healthcare.medicalrecord.domain.RecordStatus;
import com.healthcare.medicalrecord.domain.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID>, JpaSpecificationExecutor<MedicalRecord> {

    Optional<MedicalRecord> findByRecordNumber(String recordNumber);

    boolean existsByRecordNumber(String recordNumber);

    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId AND m.deleted = false " +
           "ORDER BY m.recordDate DESC")
    Page<MedicalRecord> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId AND m.recordType = :recordType " +
           "AND m.deleted = false ORDER BY m.recordDate DESC")
    Page<MedicalRecord> findByPatientIdAndRecordType(
        @Param("patientId") UUID patientId,
        @Param("recordType") RecordType recordType,
        Pageable pageable
    );

    @Query("SELECT m FROM MedicalRecord m WHERE m.providerId = :providerId AND m.deleted = false " +
           "ORDER BY m.recordDate DESC")
    Page<MedicalRecord> findByProviderId(@Param("providerId") UUID providerId, Pageable pageable);

    List<MedicalRecord> findByAppointmentIdAndDeletedFalse(UUID appointmentId);

    Page<MedicalRecord> findByStatusAndDeletedFalse(RecordStatus status, Pageable pageable);

    @Query("SELECT m FROM MedicalRecord m WHERE m.providerId = :providerId AND m.status = 'DRAFT' " +
           "AND m.deleted = false ORDER BY m.recordDate DESC")
    List<MedicalRecord> findDraftRecordsByProvider(@Param("providerId") UUID providerId);

    @Query("SELECT m FROM MedicalRecord m WHERE m.recordDate >= :startDate AND m.recordDate <= :endDate " +
           "AND m.deleted = false ORDER BY m.recordDate DESC")
    Page<MedicalRecord> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );

    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId " +
           "AND m.recordDate >= :startDate AND m.recordDate <= :endDate " +
           "AND m.deleted = false ORDER BY m.recordDate DESC")
    List<MedicalRecord> findByPatientIdAndDateRange(
        @Param("patientId") UUID patientId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    long countByPatientIdAndDeletedFalse(UUID patientId);

    long countByProviderIdAndDeletedFalse(UUID providerId);

    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId " +
           "AND m.vitalSigns IS NOT NULL AND m.deleted = false " +
           "ORDER BY m.recordDate DESC")
    List<MedicalRecord> findRecordsWithVitals(@Param("patientId") UUID patientId);

    @Query("SELECT DISTINCT m FROM MedicalRecord m JOIN m.diagnoses d " +
           "WHERE d.code = :diagnosisCode AND m.deleted = false " +
           "ORDER BY m.recordDate DESC")
    Page<MedicalRecord> findByDiagnosisCode(@Param("diagnosisCode") String diagnosisCode, Pageable pageable);

    @Query("SELECT m FROM MedicalRecord m WHERE m.patientId = :patientId AND m.deleted = false " +
           "ORDER BY m.recordDate DESC")
    List<MedicalRecord> findRecentByPatientId(@Param("patientId") UUID patientId, Pageable pageable);
}
