package com.healthcare.patient.domain;

import com.healthcare.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "patient_documents", indexes = {
    @Index(name = "idx_doc_patient_id", columnList = "patient_id"),
    @Index(name = "idx_doc_type", columnList = "document_type"),
    @Index(name = "idx_doc_uploaded_at", columnList = "uploaded_at")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientDocument extends BaseEntity {

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "object_key", nullable = false, length = 500)
    private String objectKey;

    @Column(name = "bucket_name", nullable = false, length = 100)
    private String bucketName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "uploaded_by", length = 100)
    private String uploadedBy;

    @Column(name = "etag", length = 100)
    private String etag;

    @Builder
    public PatientDocument(
            UUID patientId,
            String fileName,
            String objectKey,
            String bucketName,
            String contentType,
            long fileSize,
            DocumentType documentType,
            String description,
            String uploadedBy,
            String etag) {
        this.patientId = patientId;
        this.fileName = fileName;
        this.objectKey = objectKey;
        this.bucketName = bucketName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.documentType = documentType;
        this.description = description;
        this.uploadedAt = Instant.now();
        this.uploadedBy = uploadedBy;
        this.etag = etag;
    }
}
