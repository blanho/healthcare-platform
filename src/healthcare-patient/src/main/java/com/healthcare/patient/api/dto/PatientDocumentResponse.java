package com.healthcare.patient.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Patient document information")
public record PatientDocumentResponse(
        @Schema(description = "Document ID", example = "doc-123e4567-e89b-12d3-a456-426614174000")
        String id,

        @Schema(description = "Patient ID", example = "123e4567-e89b-12d3-a456-426614174000")
        String patientId,

        @Schema(description = "Original file name", example = "lab-results.pdf")
        String fileName,

        @Schema(description = "Storage object key", example = "patients/123/2026/02/lab-results-abc123.pdf")
        String objectKey,

        @Schema(description = "MIME content type", example = "application/pdf")
        String contentType,

        @Schema(description = "File size in bytes", example = "102400")
        long size,

        @Schema(description = "Upload timestamp")
        Instant uploadedAt,

        @Schema(description = "Presigned download URL (expires in 15 minutes)")
        String downloadUrl
) {

    public static PatientDocumentResponse of(
            String id,
            String patientId,
            String fileName,
            String objectKey,
            String contentType,
            long size,
            Instant uploadedAt,
            String downloadUrl) {
        return new PatientDocumentResponse(
                id, patientId, fileName, objectKey, contentType, size, uploadedAt, downloadUrl);
    }
}
