package com.healthcare.patient.repository;

import com.healthcare.patient.domain.DocumentType;
import com.healthcare.patient.domain.PatientDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientDocumentRepository extends JpaRepository<PatientDocument, UUID> {

    Page<PatientDocument> findByPatientId(UUID patientId, Pageable pageable);

    Page<PatientDocument> findByPatientIdAndDocumentType(
            UUID patientId, DocumentType documentType, Pageable pageable);

    Optional<PatientDocument> findByObjectKey(String objectKey);

    long countByPatientId(UUID patientId);

    List<PatientDocument> findAllByPatientId(UUID patientId);
}
