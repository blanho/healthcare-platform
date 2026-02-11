package com.healthcare.patient.service;

import com.healthcare.common.storage.StorageFacade;
import com.healthcare.common.storage.StorageResult;
import com.healthcare.patient.api.dto.PatientDocumentResponse;
import com.healthcare.patient.domain.DocumentType;
import com.healthcare.patient.domain.PatientDocument;
import com.healthcare.patient.exception.DocumentNotFoundException;
import com.healthcare.patient.exception.PatientNotFoundException;
import com.healthcare.patient.repository.PatientDocumentRepository;
import com.healthcare.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientDocumentServiceImpl implements PatientDocumentService {

    private final PatientDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final StorageFacade storageFacade;

    @Override
    @Transactional
    public PatientDocumentResponse uploadDocument(
            UUID patientId,
            MultipartFile file,
            DocumentType documentType,
            String description,
            String uploadedBy) {

        validatePatientExists(patientId);

        Map<String, String> metadata = buildMetadata(patientId, documentType, uploadedBy);

        StorageResult result = storageFacade.uploadPatientDocument(
                patientId.toString(), file, metadata);

        PatientDocument document = createDocumentEntity(
                patientId, file, documentType, description, uploadedBy, result);

        PatientDocument saved = documentRepository.save(document);

        log.info("Uploaded document {} for patient {}", saved.getId(), patientId);

        return toResponse(saved);
    }

    @Override
    public Optional<PatientDocumentResponse> getDocument(UUID documentId) {
        return documentRepository.findById(documentId)
                .map(this::toResponse);
    }

    @Override
    public Page<PatientDocumentResponse> listDocuments(UUID patientId, Pageable pageable) {
        validatePatientExists(patientId);
        return documentRepository.findByPatientId(patientId, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<PatientDocumentResponse> listDocumentsByType(
            UUID patientId, DocumentType documentType, Pageable pageable) {
        validatePatientExists(patientId);
        return documentRepository.findByPatientIdAndDocumentType(patientId, documentType, pageable)
                .map(this::toResponse);
    }

    @Override
    public String generateDownloadUrl(UUID documentId) {
        PatientDocument document = findDocumentOrThrow(documentId);
        return storageFacade.getDocumentPresignedUrl(document.getObjectKey());
    }

    @Override
    @Transactional
    public void deleteDocument(UUID documentId) {
        PatientDocument document = findDocumentOrThrow(documentId);

        storageFacade.deleteDocument(document.getObjectKey());
        documentRepository.delete(document);

        log.info("Deleted document {} for patient {}", documentId, document.getPatientId());
    }

    @Override
    @Transactional
    public int deleteAllDocumentsForPatient(UUID patientId) {
        List<PatientDocument> documents = documentRepository.findAllByPatientId(patientId);

        for (PatientDocument doc : documents) {
            storageFacade.deleteDocument(doc.getObjectKey());
        }

        documentRepository.deleteAll(documents);

        log.info("Deleted {} documents for patient {}", documents.size(), patientId);

        return documents.size();
    }

    private void validatePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw PatientNotFoundException.byId(patientId);
        }
    }

    private PatientDocument findDocumentOrThrow(UUID documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    private Map<String, String> buildMetadata(
            UUID patientId, DocumentType documentType, String uploadedBy) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("patientId", patientId.toString());
        metadata.put("documentType", documentType.name());
        metadata.put("uploadedBy", uploadedBy);
        return metadata;
    }

    private PatientDocument createDocumentEntity(
            UUID patientId,
            MultipartFile file,
            DocumentType documentType,
            String description,
            String uploadedBy,
            StorageResult result) {
        return PatientDocument.builder()
                .patientId(patientId)
                .fileName(file.getOriginalFilename())
                .objectKey(result.objectKey())
                .bucketName(result.bucketName())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .documentType(documentType)
                .description(description)
                .uploadedBy(uploadedBy)
                .etag(result.etag())
                .build();
    }

    private PatientDocumentResponse toResponse(PatientDocument document) {
        String downloadUrl = storageFacade.getDocumentPresignedUrl(document.getObjectKey());

        return PatientDocumentResponse.of(
                document.getId().toString(),
                document.getPatientId().toString(),
                document.getFileName(),
                document.getObjectKey(),
                document.getContentType(),
                document.getFileSize(),
                document.getUploadedAt(),
                downloadUrl
        );
    }
}
