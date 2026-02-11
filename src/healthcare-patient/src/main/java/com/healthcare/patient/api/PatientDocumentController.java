package com.healthcare.patient.api;

import com.healthcare.common.api.PageResponse;
import com.healthcare.patient.api.dto.PatientDocumentResponse;
import com.healthcare.patient.api.dto.UploadDocumentRequest;
import com.healthcare.patient.domain.DocumentType;
import com.healthcare.patient.exception.DocumentNotFoundException;
import com.healthcare.patient.service.PatientDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient Documents", description = "Patient document management API")
public class PatientDocumentController {

    private final PatientDocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('patient:document:write')")
    @Operation(summary = "Upload document", description = "Upload a document for a patient")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or request",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "413", description = "File too large",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientDocumentResponse> uploadDocument(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "File to upload") @RequestPart("file") MultipartFile file,
            @Parameter(description = "Document type") @RequestParam("documentType") String documentType,
            @Parameter(description = "Document description") @RequestParam(value = "description", required = false) String description,
            @AuthenticationPrincipal UserDetails user) {

        log.info("REST request to upload document for patient {}", patientId);

        DocumentType type = parseDocumentType(documentType);
        String uploadedBy = user != null ? user.getUsername() : "system";

        PatientDocumentResponse response = documentService.uploadDocument(
                patientId, file, type, description, uploadedBy);

        return ResponseEntity
                .created(URI.create("/api/v1/patients/" + patientId + "/documents/" + response.id()))
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('patient:document:read')")
    @Operation(summary = "List documents", description = "List all documents for a patient")
    @ApiResponse(responseCode = "200", description = "Documents retrieved successfully")
    public ResponseEntity<PageResponse<PatientDocumentResponse>> listDocuments(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Filter by document type") @RequestParam(required = false) String documentType,
            @PageableDefault(size = 20, sort = "uploadedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.debug("REST request to list documents for patient {}", patientId);

        Page<PatientDocumentResponse> page;
        if (documentType != null) {
            DocumentType type = parseDocumentType(documentType);
            page = documentService.listDocumentsByType(patientId, type, pageable);
        } else {
            page = documentService.listDocuments(patientId, pageable);
        }

        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("hasAuthority('patient:document:read')")
    @Operation(summary = "Get document", description = "Get document details by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document found"),
        @ApiResponse(responseCode = "404", description = "Document not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientDocumentResponse> getDocument(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Document ID") @PathVariable UUID documentId) {

        log.debug("REST request to get document {} for patient {}", documentId, patientId);

        return documentService.getDocument(documentId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    @GetMapping("/{documentId}/download-url")
    @PreAuthorize("hasAuthority('patient:document:read')")
    @Operation(summary = "Get download URL", description = "Generate a presigned download URL for the document")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Download URL generated"),
        @ApiResponse(responseCode = "404", description = "Document not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<DownloadUrlResponse> getDownloadUrl(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Document ID") @PathVariable UUID documentId) {

        log.debug("REST request to get download URL for document {} for patient {}", documentId, patientId);

        String url = documentService.generateDownloadUrl(documentId);
        return ResponseEntity.ok(new DownloadUrlResponse(url));
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('patient:document:delete')")
    @Operation(summary = "Delete document", description = "Delete a patient document")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Document deleted"),
        @ApiResponse(responseCode = "404", description = "Document not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @Parameter(description = "Document ID") @PathVariable UUID documentId) {

        log.info("REST request to delete document {} for patient {}", documentId, patientId);

        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    private DocumentType parseDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid document type: " + documentType +
                    ". Valid values: LAB_RESULT, IMAGING, CONSENT_FORM, INSURANCE, PRESCRIPTION, REFERRAL, DISCHARGE_SUMMARY, MEDICAL_HISTORY, VACCINATION, OTHER");
        }
    }

    @Schema(description = "Download URL response")
    public record DownloadUrlResponse(
            @Schema(description = "Presigned URL for document download (expires in 15 minutes)")
            String downloadUrl
    ) {}
}
