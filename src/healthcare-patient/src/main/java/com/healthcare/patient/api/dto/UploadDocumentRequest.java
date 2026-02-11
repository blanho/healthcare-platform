package com.healthcare.patient.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Document upload metadata")
public record UploadDocumentRequest(
        @Schema(description = "Document type", example = "LAB_RESULT",
                allowableValues = {"LAB_RESULT", "IMAGING", "CONSENT_FORM", "INSURANCE", "PRESCRIPTION", "OTHER"})
        @NotBlank(message = "Document type is required")
        String documentType,

        @Schema(description = "Document description", example = "Blood test results from annual checkup")
        @Size(max = 500, message = "Description must be less than 500 characters")
        String description
) {
}
