package com.healthcare.billing.service;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ClaimService {

    ClaimResponse submitClaim(SubmitClaimRequest request, String createdBy);

    ClaimResponse getClaim(UUID claimId);

    ClaimResponse getClaimByNumber(String claimNumber);

    ClaimResponse getClaimForInvoice(UUID invoiceId);

    Page<ClaimResponse> getPatientClaims(UUID patientId, Pageable pageable);

    Page<ClaimResponse> getClaimsByStatus(ClaimStatus status, Pageable pageable);

    Page<ClaimResponse> getPendingClaims(Pageable pageable);

    Page<ClaimResponse> getClaimsByProvider(String provider, Pageable pageable);

    ClaimResponse processClaim(UUID claimId, ProcessClaimRequest request, String processedBy);

    ClaimResponse appealClaim(UUID claimId, String appealNotes, String processedBy);

    ClaimResponse markClaimPaid(UUID claimId, String eobReference, String processedBy);

    ClaimResponse closeClaim(UUID claimId, String processedBy);

    List<ClaimResponse> getPendingClaimsForPatient(UUID patientId);
}
