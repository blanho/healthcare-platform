package com.healthcare.billing.api;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.PaymentMethod;
import com.healthcare.billing.domain.PaymentStatus;
import com.healthcare.billing.service.PaymentService;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Record a new payment")
    public ResponseEntity<PaymentResponse> recordPayment(
            @Valid @RequestBody RecordPaymentRequest request,
            @AuthenticationPrincipal UserDetails user) {
        PaymentResponse response = paymentService.recordPayment(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPayment(
            @Parameter(description = "Payment ID") @PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping("/reference/{referenceNumber}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payment by reference number")
    public ResponseEntity<PaymentResponse> getPaymentByReference(
            @Parameter(description = "Reference number") @PathVariable String referenceNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(referenceNumber));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payments for an invoice")
    public ResponseEntity<List<PaymentResponse>> getInvoicePayments(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(paymentService.getInvoicePayments(invoiceId));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payments for a patient")
    public ResponseEntity<Page<PaymentResponse>> getPatientPayments(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPatientPayments(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByStatus(
            @Parameter(description = "Payment status") @PathVariable PaymentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status, pageable));
    }

    @GetMapping("/method/{method}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payments by method")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByMethod(
            @Parameter(description = "Payment method") @PathVariable PaymentMethod method,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsByMethod(method, pageable));
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentResponse> refundPayment(
            @Parameter(description = "Payment ID") @PathVariable UUID paymentId,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(paymentService.refundPayment(paymentId, amount, user.getUsername()));
    }

    @GetMapping("/patient/{patientId}/history")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get patient's payment history")
    public ResponseEntity<List<PaymentResponse>> getPatientPaymentHistory(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId) {
        return ResponseEntity.ok(paymentService.getPatientPaymentHistory(patientId));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get payments in date range")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsInDateRange(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getPaymentsInDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Calculate revenue in period")
    public ResponseEntity<BigDecimal> calculateRevenue(
            @RequestParam Instant startDate,
            @RequestParam Instant endDate) {
        return ResponseEntity.ok(paymentService.calculateRevenueInPeriod(startDate, endDate));
    }
}
