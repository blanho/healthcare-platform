package com.healthcare.provider.api;

import com.healthcare.common.api.PageResponse;
import com.healthcare.provider.api.dto.CreateProviderRequest;
import com.healthcare.provider.api.dto.ProviderResponse;
import com.healthcare.provider.api.dto.ProviderSearchCriteria;
import com.healthcare.provider.api.dto.ProviderSummaryResponse;
import com.healthcare.provider.api.dto.ScheduleRequest;
import com.healthcare.provider.api.dto.UpdateProviderRequest;
import com.healthcare.provider.domain.ProviderStatus;
import com.healthcare.provider.domain.ProviderType;
import com.healthcare.provider.exception.ProviderNotFoundException;
import com.healthcare.provider.service.ProviderService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Providers", description = "Provider management API")
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Create a new provider", description = "Creates a new healthcare provider record")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Provider created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Provider with email/license already exists",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> createProvider(
            @Valid @RequestBody CreateProviderRequest request) {
        log.info("REST request to create provider");

        ProviderResponse response = providerService.createProvider(request);

        return ResponseEntity
            .created(URI.create("/api/v1/providers/" + response.id()))
            .body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "Get provider by ID", description = "Retrieves a provider by their unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider found"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> getProviderById(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.debug("REST request to get provider by ID: {}", id);

        return providerService.getProviderById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> ProviderNotFoundException.byId(id));
    }

    @GetMapping("/number/{providerNumber}")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "Get provider by provider number", description = "Retrieves a provider by their provider number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider found"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> getProviderByNumber(
            @Parameter(description = "Provider Number") @PathVariable String providerNumber) {
        log.debug("REST request to get provider by number: {}", providerNumber);

        return providerService.getProviderByProviderNumber(providerNumber)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> ProviderNotFoundException.byProviderNumber(providerNumber));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "List all providers", description = "Retrieves a paginated list of all providers")
    @ApiResponse(responseCode = "200", description = "Providers retrieved successfully")
    public ResponseEntity<PageResponse<ProviderSummaryResponse>> listProviders(
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("REST request to list providers with pageable: {}", pageable);

        PageResponse<ProviderSummaryResponse> response = providerService.listProviders(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "Search providers", description = "Searches providers based on provided criteria")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<PageResponse<ProviderSummaryResponse>> searchProviders(
            @Parameter(description = "Name to search (first or last name)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Email to search")
            @RequestParam(required = false) String email,

            @Parameter(description = "Provider type filter")
            @RequestParam(required = false) ProviderType providerType,

            @Parameter(description = "Specialization filter")
            @RequestParam(required = false) String specialization,

            @Parameter(description = "Status filter")
            @RequestParam(required = false) ProviderStatus status,

            @Parameter(description = "Accepting patients filter")
            @RequestParam(required = false) Boolean acceptingPatients,

            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("REST request to search providers");

        ProviderSearchCriteria criteria = new ProviderSearchCriteria(
            name, email, providerType, specialization, status, acceptingPatients
        );

        PageResponse<ProviderSummaryResponse> response = providerService.searchProviders(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accepting-patients")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "List providers accepting patients", description = "Retrieves providers currently accepting new patients")
    @ApiResponse(responseCode = "200", description = "Providers retrieved successfully")
    public ResponseEntity<PageResponse<ProviderSummaryResponse>> listAcceptingPatients(
            @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC)
            Pageable pageable) {
        log.debug("REST request to list providers accepting patients");

        PageResponse<ProviderSummaryResponse> response = providerService.listAcceptingPatients(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specializations")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "Get all specializations", description = "Retrieves all unique provider specializations")
    @ApiResponse(responseCode = "200", description = "Specializations retrieved successfully")
    public ResponseEntity<List<String>> getAllSpecializations() {
        log.debug("REST request to get all specializations");

        List<String> specializations = providerService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Update provider", description = "Updates an existing provider with the provided information")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> updateProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateProviderRequest request) {
        log.info("REST request to update provider with ID: {}", id);

        ProviderResponse response = providerService.updateProvider(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Activate provider", description = "Activates a provider account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider activated successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Invalid status transition",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> activateProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to activate provider with ID: {}", id);

        ProviderResponse response = providerService.activateProvider(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Deactivate provider", description = "Deactivates a provider account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> deactivateProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to deactivate provider with ID: {}", id);

        ProviderResponse response = providerService.deactivateProvider(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/on-leave")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Put provider on leave", description = "Sets provider status to on leave")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider put on leave successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> putProviderOnLeave(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to put provider on leave with ID: {}", id);

        ProviderResponse response = providerService.putProviderOnLeave(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/return-from-leave")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Return provider from leave", description = "Returns provider from leave status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider returned from leave successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Provider is not on leave",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> returnProviderFromLeave(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to return provider from leave with ID: {}", id);

        ProviderResponse response = providerService.returnProviderFromLeave(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend provider", description = "Suspends a provider account (admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider suspended successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> suspendProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to suspend provider with ID: {}", id);

        ProviderResponse response = providerService.suspendProvider(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/schedules")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Add schedule", description = "Adds a schedule to a provider")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Schedule added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Schedule already exists for day",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> addSchedule(
            @Parameter(description = "Provider ID") @PathVariable UUID id,
            @Valid @RequestBody ScheduleRequest request) {
        log.info("REST request to add schedule to provider: {} for {}", id, request.dayOfWeek());

        ProviderResponse response = providerService.addSchedule(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{providerId}/schedules/{scheduleId}")
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Update schedule", description = "Updates a provider's schedule")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Schedule updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Provider or schedule not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProviderResponse> updateSchedule(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId,
            @Parameter(description = "Schedule ID") @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleRequest request) {
        log.info("REST request to update schedule {} for provider: {}", scheduleId, providerId);

        ProviderResponse response = providerService.updateSchedule(providerId, scheduleId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{providerId}/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('provider:write')")
    @Operation(summary = "Remove schedule", description = "Removes a schedule from a provider")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Schedule removed successfully"),
        @ApiResponse(responseCode = "404", description = "Provider or schedule not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> removeSchedule(
            @Parameter(description = "Provider ID") @PathVariable UUID providerId,
            @Parameter(description = "Schedule ID") @PathVariable UUID scheduleId) {
        log.info("REST request to remove schedule {} from provider: {}", scheduleId, providerId);

        providerService.removeSchedule(providerId, scheduleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('provider:delete')")
    @Operation(summary = "Delete provider", description = "Soft deletes a provider record")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Provider deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Provider not found",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deleteProvider(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.info("REST request to delete provider with ID: {}", id);

        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/can-schedule")
    @PreAuthorize("hasAuthority('provider:read')")
    @Operation(summary = "Check if provider can be scheduled",
        description = "Checks if provider is eligible for scheduling appointments")
    @ApiResponse(responseCode = "200", description = "Eligibility check completed")
    public ResponseEntity<Boolean> canBeScheduled(
            @Parameter(description = "Provider ID") @PathVariable UUID id) {
        log.debug("REST request to check if provider {} can be scheduled", id);

        boolean canSchedule = providerService.canBeScheduled(id);
        return ResponseEntity.ok(canSchedule);
    }
}
