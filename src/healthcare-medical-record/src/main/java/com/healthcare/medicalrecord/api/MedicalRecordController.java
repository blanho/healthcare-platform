package com.healthcare.medicalrecord.api;

import com.healthcare.medicalrecord.api.dto.*;
import com.healthcare.medicalrecord.domain.RecordStatus;
import com.healthcare.medicalrecord.domain.RecordType;
import com.healthcare.medicalrecord.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/medical-records")
@Tag(name = "Medical Records", description = "Medical record management API (PHI)")
public class MedicalRecordController {

    private static final Logger log = LoggerFactory.getLogger(MedicalRecordController.class);

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Create a new medical record",
               description = "Creates a new medical record in DRAFT status")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Medical record created"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MedicalRecordResponse> create(
            @Valid @RequestBody CreateMedicalRecordRequest request) {
        log.info("REST request to create medical record for patient: {}", request.patientId());
        MedicalRecordResponse response = medicalRecordService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{recordId}")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Update a medical record",
               description = "Updates a draft medical record")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medical record updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request or record not editable"),
        @ApiResponse(responseCode = "404", description = "Medical record not found")
    })
    public ResponseEntity<MedicalRecordResponse> update(
            @PathVariable UUID recordId,
            @Valid @RequestBody UpdateMedicalRecordRequest request) {
        log.info("REST request to update medical record: {}", recordId);
        MedicalRecordResponse response = medicalRecordService.update(recordId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recordId}")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get medical record by ID",
               description = "Retrieves detailed medical record by ID (PHI access logged)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medical record found"),
        @ApiResponse(responseCode = "404", description = "Medical record not found")
    })
    public ResponseEntity<MedicalRecordResponse> getById(
            @PathVariable UUID recordId) {
        log.info("REST request to get medical record: {}", recordId);
        MedicalRecordResponse response = medicalRecordService.getById(recordId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-number/{recordNumber}")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get medical record by number",
               description = "Retrieves medical record by its unique record number")
    public ResponseEntity<MedicalRecordResponse> getByRecordNumber(
            @PathVariable String recordNumber) {
        log.info("REST request to get medical record by number: {}", recordNumber);
        MedicalRecordResponse response = medicalRecordService.getByRecordNumber(recordNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Search medical records",
               description = "Search medical records with various criteria")
    public ResponseEntity<Page<MedicalRecordSummaryResponse>> search(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Filter by provider ID")
            @RequestParam(required = false) UUID providerId,
            @Parameter(description = "Filter by appointment ID")
            @RequestParam(required = false) UUID appointmentId,
            @Parameter(description = "Filter by record type")
            @RequestParam(required = false) RecordType recordType,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) RecordStatus status,
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Filter by diagnosis code")
            @RequestParam(required = false) String diagnosisCode,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("REST request to search medical records");

        MedicalRecordSearchCriteria criteria = new MedicalRecordSearchCriteria(
                patientId, providerId, appointmentId, recordType, status, startDate, endDate, diagnosisCode
        );

        Page<MedicalRecordSummaryResponse> page = medicalRecordService.search(criteria, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get patient medical records",
               description = "Retrieves all medical records for a patient")
    public ResponseEntity<Page<MedicalRecordSummaryResponse>> getByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get medical records for patient: {}", patientId);
        Page<MedicalRecordSummaryResponse> page = medicalRecordService.getByPatient(patientId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/patient/{patientId}/timeline")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get patient timeline",
               description = "Retrieves recent medical records for patient timeline view")
    public ResponseEntity<List<MedicalRecordSummaryResponse>> getPatientTimeline(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("REST request to get patient timeline: {}", patientId);
        List<MedicalRecordSummaryResponse> timeline = medicalRecordService.getPatientTimeline(patientId, limit);
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get provider medical records",
               description = "Retrieves all medical records created by a provider")
    public ResponseEntity<Page<MedicalRecordSummaryResponse>> getByProvider(
            @PathVariable UUID providerId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get medical records for provider: {}", providerId);
        Page<MedicalRecordSummaryResponse> page = medicalRecordService.getByProvider(providerId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/provider/{providerId}/drafts")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get draft records for provider",
               description = "Retrieves draft records needing finalization")
    public ResponseEntity<List<MedicalRecordSummaryResponse>> getDraftRecords(
            @PathVariable UUID providerId) {
        log.debug("REST request to get draft records for provider: {}", providerId);
        List<MedicalRecordSummaryResponse> drafts = medicalRecordService.getDraftRecords(providerId);
        return ResponseEntity.ok(drafts);
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAuthority('medical_record:read')")
    @Operation(summary = "Get records for appointment",
               description = "Retrieves all medical records for an appointment")
    public ResponseEntity<List<MedicalRecordSummaryResponse>> getByAppointment(
            @PathVariable UUID appointmentId) {
        log.debug("REST request to get medical records for appointment: {}", appointmentId);
        List<MedicalRecordSummaryResponse> records = medicalRecordService.getByAppointment(appointmentId);
        return ResponseEntity.ok(records);
    }

    @PatchMapping("/{recordId}/finalize")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Finalize medical record",
               description = "Finalizes a draft record, making it read-only")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record finalized"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<MedicalRecordResponse> finalize(
            @PathVariable UUID recordId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to finalize medical record: {}", recordId);
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        MedicalRecordResponse response = medicalRecordService.finalize(recordId, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}/amend")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Amend medical record",
               description = "Amends a finalized record with additional information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record amended"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<MedicalRecordResponse> amend(
            @PathVariable UUID recordId,
            @Valid @RequestBody AmendRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to amend medical record: {}", recordId);
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        MedicalRecordResponse response = medicalRecordService.amend(recordId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}/void")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Void medical record",
               description = "Voids a record (marks as entered in error)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Record voided"),
        @ApiResponse(responseCode = "400", description = "Invalid state transition"),
        @ApiResponse(responseCode = "404", description = "Record not found")
    })
    public ResponseEntity<MedicalRecordResponse> voidRecord(
            @PathVariable UUID recordId,
            @Valid @RequestBody VoidRecordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to void medical record: {}", recordId);
        String userId = userDetails != null ? userDetails.getUsername() : "system";
        MedicalRecordResponse response = medicalRecordService.voidRecord(recordId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}/vitals")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Add/update vital signs",
               description = "Records vital signs for a medical record")
    public ResponseEntity<MedicalRecordResponse> addVitals(
            @PathVariable UUID recordId,
            @Valid @RequestBody VitalSignsRequest request) {
        log.info("REST request to add vitals to record: {}", recordId);
        MedicalRecordResponse response = medicalRecordService.addVitals(recordId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{recordId}/soap-note")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Update SOAP note",
               description = "Updates the SOAP note for a medical record")
    public ResponseEntity<MedicalRecordResponse> updateSoapNote(
            @PathVariable UUID recordId,
            @Valid @RequestBody SoapNoteRequest request) {
        log.info("REST request to update SOAP note for record: {}", recordId);
        MedicalRecordResponse response = medicalRecordService.updateSoapNote(recordId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{recordId}/diagnoses")
    @PreAuthorize("hasAuthority('medical_record:write')")
    @Operation(summary = "Add diagnosis",
               description = "Adds a diagnosis to a medical record")
    public ResponseEntity<MedicalRecordResponse> addDiagnosis(
            @PathVariable UUID recordId,
            @Valid @RequestBody DiagnosisRequest request) {
        log.info("REST request to add diagnosis to record: {}", recordId);
        MedicalRecordResponse response = medicalRecordService.addDiagnosis(recordId, request);
        return ResponseEntity.ok(response);
    }
}
