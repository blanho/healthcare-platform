package com.healthcare.billing.api;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.ClaimStatus;
import com.healthcare.billing.service.ClaimService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
@Tag(name = "Insurance Claims", description = "Insurance claim management endpoints")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Submit a new insurance claim")
    public ResponseEntity<ClaimResponse> submitClaim(
            @Valid @RequestBody SubmitClaimRequest request,
            @AuthenticationPrincipal UserDetails user) {
        ClaimResponse response = claimService.submitClaim(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{claimId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claim by ID")
    public ResponseEntity<ClaimResponse> getClaim(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId) {
        return ResponseEntity.ok(claimService.getClaim(claimId));
    }

    @GetMapping("/number/{claimNumber}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claim by claim number")
    public ResponseEntity<ClaimResponse> getClaimByNumber(
            @Parameter(description = "Claim number") @PathVariable String claimNumber) {
        return ResponseEntity.ok(claimService.getClaimByNumber(claimNumber));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claim for an invoice")
    public ResponseEntity<ClaimResponse> getClaimForInvoice(
            @Parameter(description = "Invoice ID") @PathVariable UUID invoiceId) {
        return ResponseEntity.ok(claimService.getClaimForInvoice(invoiceId));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claims for a patient")
    public ResponseEntity<Page<ClaimResponse>> getPatientClaims(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(claimService.getPatientClaims(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claims by status")
    public ResponseEntity<Page<ClaimResponse>> getClaimsByStatus(
            @Parameter(description = "Claim status") @PathVariable ClaimStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(claimService.getClaimsByStatus(status, pageable));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get pending claims")
    public ResponseEntity<Page<ClaimResponse>> getPendingClaims(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(claimService.getPendingClaims(pageable));
    }

    @GetMapping("/provider/{provider}")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get claims by insurance provider")
    public ResponseEntity<Page<ClaimResponse>> getClaimsByProvider(
            @Parameter(description = "Insurance provider") @PathVariable String provider,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(claimService.getClaimsByProvider(provider, pageable));
    }

    @PostMapping("/{claimId}/process")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Process claim adjudication")
    public ResponseEntity<ClaimResponse> processClaim(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId,
            @Valid @RequestBody ProcessClaimRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(claimService.processClaim(claimId, request, user.getUsername()));
    }

    @PostMapping("/{claimId}/appeal")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Appeal a denied claim")
    public ResponseEntity<ClaimResponse> appealClaim(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId,
            @RequestParam String appealNotes,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(claimService.appealClaim(claimId, appealNotes, user.getUsername()));
    }

    @PostMapping("/{claimId}/paid")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Mark claim as paid")
    public ResponseEntity<ClaimResponse> markClaimPaid(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId,
            @RequestParam String eobReference,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(claimService.markClaimPaid(claimId, eobReference, user.getUsername()));
    }

    @PostMapping("/{claimId}/close")
    @PreAuthorize("hasAuthority('billing:write')")
    @Operation(summary = "Close a claim")
    public ResponseEntity<ClaimResponse> closeClaim(
            @Parameter(description = "Claim ID") @PathVariable UUID claimId,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(claimService.closeClaim(claimId, user.getUsername()));
    }

    @GetMapping("/patient/{patientId}/pending")
    @PreAuthorize("hasAuthority('billing:read')")
    @Operation(summary = "Get pending claims for a patient")
    public ResponseEntity<List<ClaimResponse>> getPendingClaimsForPatient(
            @Parameter(description = "Patient ID") @PathVariable UUID patientId) {
        return ResponseEntity.ok(claimService.getPendingClaimsForPatient(patientId));
    }
}
