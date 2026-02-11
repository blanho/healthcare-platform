package com.healthcare.patient.api;

import com.healthcare.common.api.PageResponse;
import com.healthcare.patient.api.dto.CreatePatientRequest;
import com.healthcare.patient.api.dto.PatientResponse;
import com.healthcare.patient.api.dto.PatientSearchCriteria;
import com.healthcare.patient.api.dto.PatientSummaryResponse;
import com.healthcare.patient.api.dto.UpdatePatientRequest;
import com.healthcare.patient.exception.PatientNotFoundException;
import com.healthcare.patient.service.PatientService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patients", description = "Patient management API")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Create a new patient", description = "Creates a new patient record with the provided information")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Patient with email already exists",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> createPatient(
            @Valid @RequestBody CreatePatientRequest request) {
        log.info("REST request to create patient");

        PatientResponse response = patientService.createPatient(request);

        return ResponseEntity
            .created(URI.create("/api/v1/patients/" + response.id()))
            .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Get patient by ID", description = "Retrieves a patient by their unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> getPatientById(
            @Parameter(description = "Patient ID") @PathVariable UUID id) {
        log.debug("REST request to get patient by ID: {}", id);

        return patientService.getPatientById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> PatientNotFoundException.byId(id));
    }

    @GetMapping("/mrn/{mrn}")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Get patient by MRN", description = "Retrieves a patient by their medical record number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> getPatientByMrn(
            @Parameter(description = "Medical Record Number") @PathVariable String mrn) {
        log.debug("REST request to get patient by MRN: {}", mrn);

        return patientService.getPatientByMrn(mrn)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> PatientNotFoundException.byMrn(mrn));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "List all patients", description = "Retrieves a paginated list of all patients")
    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully")
    public ResponseEntity<PageResponse<PatientSummaryResponse>> listPatients(
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("REST request to list patients with pageable: {}", pageable);

        PageResponse<PatientSummaryResponse> response = patientService.listPatients(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Search patients", description = "Searches patients based on provided criteria")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<PageResponse<PatientSummaryResponse>> searchPatients(
            @Parameter(description = "Name to search (first or last name)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Email to search")
            @RequestParam(required = false) String email,

            @Parameter(description = "Phone number to search")
            @RequestParam(required = false) String phoneNumber,

            @Parameter(description = "Medical record number")
            @RequestParam(required = false) String medicalRecordNumber,

            @Parameter(description = "Patient status filter")
            @RequestParam(required = false) String status,

            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("REST request to search patients");

        PatientSearchCriteria criteria = new PatientSearchCriteria(
            name, email, phoneNumber, medicalRecordNumber,
            status != null ? com.healthcare.patient.domain.PatientStatus.valueOf(status.toUpperCase()) : null
        );

        PageResponse<PatientSummaryResponse> response = patientService.searchPatients(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Update patient", description = "Updates an existing patient with the provided information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {
        log.info("REST request to update patient with ID: {}", id);

        PatientResponse response = patientService.updatePatient(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Activate patient", description = "Activates a patient account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient activated successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> activatePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id) {
        log.info("REST request to activate patient with ID: {}", id);

        PatientResponse response = patientService.activatePatient(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('patient:write')")
    @Operation(summary = "Deactivate patient", description = "Deactivates a patient account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PatientResponse> deactivatePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id) {
        log.info("REST request to deactivate patient with ID: {}", id);

        PatientResponse response = patientService.deactivatePatient(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('patient:delete')")
    @Operation(summary = "Delete patient", description = "Soft deletes a patient record")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "Patient ID") @PathVariable UUID id) {
        log.info("REST request to delete patient with ID: {}", id);

        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/can-schedule")
    @PreAuthorize("hasAuthority('patient:read')")
    @Operation(summary = "Check if patient can schedule",
        description = "Checks if patient is eligible to schedule appointments")
    @ApiResponse(responseCode = "200", description = "Eligibility check completed")
    public ResponseEntity<Boolean> canScheduleAppointments(
            @Parameter(description = "Patient ID") @PathVariable UUID id) {
        log.debug("REST request to check if patient {} can schedule appointments", id);

        boolean canSchedule = patientService.canScheduleAppointments(id);
        return ResponseEntity.ok(canSchedule);
    }
}
