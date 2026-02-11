package com.healthcare.patient.service;

import com.healthcare.patient.api.dto.PatientDocumentResponse;
import com.healthcare.patient.domain.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

public interface PatientDocumentService {

    PatientDocumentResponse uploadDocument(
            UUID patientId,
            MultipartFile file,
            DocumentType documentType,
            String description,
            String uploadedBy);

    Optional<PatientDocumentResponse> getDocument(UUID documentId);

    Page<PatientDocumentResponse> listDocuments(UUID patientId, Pageable pageable);

    Page<PatientDocumentResponse> listDocumentsByType(
            UUID patientId, DocumentType documentType, Pageable pageable);

    String generateDownloadUrl(UUID documentId);

    void deleteDocument(UUID documentId);

    int deleteAllDocumentsForPatient(UUID patientId);
}
