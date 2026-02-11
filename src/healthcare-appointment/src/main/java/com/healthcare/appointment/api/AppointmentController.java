package com.healthcare.appointment.api;

import com.healthcare.appointment.api.dto.*;
import com.healthcare.appointment.domain.AppointmentStatus;
import com.healthcare.appointment.domain.AppointmentType;
import com.healthcare.appointment.service.AppointmentService;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@Tag(name = "Appointments", description = "Appointment management API")
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Schedule a new appointment",
               description = "Creates a new appointment for a patient with a provider")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Appointment scheduled successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Time slot conflict")
    })
    public ResponseEntity<AppointmentResponse> schedule(
            @Valid @RequestBody ScheduleAppointmentRequest request) {
        log.info("REST request to schedule appointment for patient {} with provider {}",
                request.patientId(), request.providerId());
        AppointmentResponse response = appointmentService.schedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{appointmentId}/reschedule")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Reschedule an appointment",
               description = "Changes the date and time of an existing appointment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment rescheduled successfully"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Time slot conflict or invalid state")
    })
    public ResponseEntity<AppointmentResponse> reschedule(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody RescheduleAppointmentRequest request) {
        log.info("REST request to reschedule appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.reschedule(appointmentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get appointment by ID",
               description = "Retrieves detailed appointment information by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponse> getById(
            @PathVariable UUID appointmentId) {
        log.debug("REST request to get appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.getById(appointmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-number/{appointmentNumber}")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get appointment by number",
               description = "Retrieves appointment by its unique appointment number")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment found"),
        @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentResponse> getByNumber(
            @PathVariable String appointmentNumber) {
        log.debug("REST request to get appointment by number: {}", appointmentNumber);
        AppointmentResponse response = appointmentService.getByAppointmentNumber(appointmentNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Search appointments",
               description = "Search appointments with various criteria")
    public ResponseEntity<Page<AppointmentSummaryResponse>> search(
            @Parameter(description = "Filter by patient ID")
            @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Filter by provider ID")
            @RequestParam(required = false) UUID providerId,
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "Filter by appointment type")
            @RequestParam(required = false) AppointmentType appointmentType,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) AppointmentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("REST request to search appointments");

        AppointmentSearchCriteria criteria = new AppointmentSearchCriteria(
                patientId, providerId, startDate, endDate, appointmentType, status
        );

        Page<AppointmentSummaryResponse> page = appointmentService.search(criteria, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get patient appointments",
               description = "Retrieves all appointments for a specific patient")
    public ResponseEntity<Page<AppointmentSummaryResponse>> getByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get appointments for patient: {}", patientId);
        Page<AppointmentSummaryResponse> page = appointmentService.getByPatient(patientId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get provider appointments",
               description = "Retrieves all appointments for a specific provider")
    public ResponseEntity<Page<AppointmentSummaryResponse>> getByProvider(
            @PathVariable UUID providerId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get appointments for provider: {}", providerId);
        Page<AppointmentSummaryResponse> page = appointmentService.getByProvider(providerId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/provider/{providerId}/today")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get today's appointments for provider",
               description = "Retrieves all appointments scheduled for today for a provider")
    public ResponseEntity<List<AppointmentSummaryResponse>> getTodaysAppointments(
            @PathVariable UUID providerId) {
        log.debug("REST request to get today's appointments for provider: {}", providerId);
        List<AppointmentSummaryResponse> appointments = appointmentService.getTodaysAppointments(providerId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Get appointments by date range",
               description = "Retrieves all appointments within a date range")
    public ResponseEntity<Page<AppointmentSummaryResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get appointments from {} to {}", startDate, endDate);
        Page<AppointmentSummaryResponse> page = appointmentService.getByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/slot-availability")
    @PreAuthorize("hasAuthority('appointment:read')")
    @Operation(summary = "Check slot availability",
               description = "Checks if a time slot is available for a provider")
    public ResponseEntity<SlotAvailabilityResponse> checkSlotAvailability(
            @RequestParam UUID providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam int durationMinutes) {
        log.debug("REST request to check slot availability for provider {} on {} at {}",
                providerId, date, startTime);
        boolean available = appointmentService.isSlotAvailable(providerId, date, startTime, durationMinutes);
        return ResponseEntity.ok(new SlotAvailabilityResponse(providerId, date, startTime, durationMinutes, available));
    }

    @PatchMapping("/{appointmentId}/confirm")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Confirm appointment",
               description = "Confirms a scheduled appointment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment confirmed"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<AppointmentResponse> confirm(
            @PathVariable UUID appointmentId) {
        log.info("REST request to confirm appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.confirm(appointmentId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/check-in")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Check in patient",
               description = "Marks the patient as checked in for the appointment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Patient checked in"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<AppointmentResponse> checkIn(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String notes) {
        log.info("REST request to check in appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.checkIn(appointmentId, notes);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/complete")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Complete appointment",
               description = "Marks the appointment as completed")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment completed"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<AppointmentResponse> complete(
            @PathVariable UUID appointmentId,
            @RequestParam(required = false) String notes) {
        log.info("REST request to complete appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.complete(appointmentId, notes);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Cancel appointment",
               description = "Cancels an appointment with a reason")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment cancelled"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable UUID appointmentId,
            @Valid @RequestBody CancelAppointmentRequest request) {
        log.info("REST request to cancel appointment: {}", appointmentId);
        AppointmentResponse response = appointmentService.cancel(appointmentId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{appointmentId}/no-show")
    @PreAuthorize("hasAuthority('appointment:write')")
    @Operation(summary = "Mark as no-show",
               description = "Marks the appointment as no-show when patient doesn't arrive")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Appointment marked as no-show"),
        @ApiResponse(responseCode = "404", description = "Appointment not found"),
        @ApiResponse(responseCode = "409", description = "Invalid state transition")
    })
    public ResponseEntity<AppointmentResponse> markNoShow(
            @PathVariable UUID appointmentId) {
        log.info("REST request to mark appointment as no-show: {}", appointmentId);
        AppointmentResponse response = appointmentService.markNoShow(appointmentId);
        return ResponseEntity.ok(response);
    }

    public record SlotAvailabilityResponse(
            UUID providerId,
            LocalDate date,
            LocalTime startTime,
            int durationMinutes,
            boolean available
    ) {}
}
