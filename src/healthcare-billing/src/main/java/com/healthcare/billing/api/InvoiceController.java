package com.healthcare.billing.api;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.InvoiceStatus;
import com.healthcare.billing.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoices", description = "Invoice management endpoints")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Create a new invoice")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request,
            @AuthenticationPrincipal UserDetails user) {
        InvoiceResponse response = invoiceService.createInvoice(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<InvoiceResponse> getInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoice(invoiceId));
    }

    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoice by invoice number")
    public ResponseEntity<InvoiceResponse> getInvoiceByNumber(
            @Parameter(description = "Invoice number") @PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoices for a patient")
    public ResponseEntity<Page<InvoiceSummaryResponse>> getPatientInvoices(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getPatientInvoices(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoices by status")
    public ResponseEntity<Page<InvoiceSummaryResponse>> getInvoicesByStatus(
            @Parameter(description = "Invoice status") @PathVariable InvoiceStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status, pageable));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get overdue invoices")
    public ResponseEntity<List<InvoiceSummaryResponse>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @PostMapping("/{invoiceId}/items")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Add item to invoice")
    public ResponseEntity<InvoiceResponse> addItem(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @Valid @RequestBody AddInvoiceItemRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.addItem(invoiceId, request, user.getUsername()));
    }

    @DeleteMapping("/{invoiceId}/items/{itemId}")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Remove item from invoice")
    public ResponseEntity<InvoiceResponse> removeItem(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @Parameter(description = "Item ID") @PathVariable UUID itemId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.removeItem(invoiceId, itemId, user.getUsername()));
    }

    @PostMapping("/{invoiceId}/discount")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Apply discount to invoice")
    public ResponseEntity<InvoiceResponse> applyDiscount(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.applyDiscount(invoiceId, amount, user.getUsername()));
    }

    @PostMapping("/{invoiceId}/discount-percentage")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Apply percentage discount to invoice")
    public ResponseEntity<InvoiceResponse> applyPercentageDiscount(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @RequestParam BigDecimal percentage,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.applyPercentageDiscount(invoiceId, percentage, user.getUsername()));
    }

    @PostMapping("/{invoiceId}/finalize")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Finalize invoice (change from DRAFT to PENDING)")
    public ResponseEntity<InvoiceResponse> finalizeInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.finalizeInvoice(invoiceId, user.getUsername()));
    }

    @PostMapping("/{invoiceId}/cancel")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Cancel invoice")
    public ResponseEntity<InvoiceResponse> cancelInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(invoiceId, user.getUsername()));
    }

    @GetMapping("/patient/{patientId}/balance")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get patient's unpaid balance")
    public ResponseEntity<BigDecimal> getPatientBalance(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId) {
        return ResponseEntity.ok(invoiceService.getPatientBalance(patientId));
    }

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoice for appointment")
    public ResponseEntity<InvoiceResponse> getInvoiceForAppointment(
            @Parameter(description = "Appointment ID") @PathVariable UUID appointmentId) {
        return ResponseEntity.ok(invoiceService.getInvoiceForAppointment(appointmentId));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get invoices by date range")
    public ResponseEntity<Page<InvoiceSummaryResponse>> getInvoicesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDateRange(startDate, endDate, pageable));
    }
}
