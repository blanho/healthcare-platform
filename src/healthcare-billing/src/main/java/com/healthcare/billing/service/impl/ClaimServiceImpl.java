package com.healthcare.billing.service.impl;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.*;
import com.healthcare.billing.domain.event.ClaimStatusChangedEvent;
import com.healthcare.billing.domain.event.ClaimSubmittedEvent;
import com.healthcare.billing.exception.ClaimNotFoundException;
import com.healthcare.billing.exception.ClaimProcessingException;
import com.healthcare.billing.exception.InvoiceNotFoundException;
import com.healthcare.billing.repository.InsuranceClaimRepository;
import com.healthcare.billing.repository.InvoiceRepository;
import com.healthcare.billing.service.ClaimNumberGenerator;
import com.healthcare.billing.service.ClaimService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);

    private final InsuranceClaimRepository claimRepository;
    private final InvoiceRepository invoiceRepository;
    private final ClaimNumberGenerator claimNumberGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public ClaimServiceImpl(InsuranceClaimRepository claimRepository,
                             InvoiceRepository invoiceRepository,
                             ClaimNumberGenerator claimNumberGenerator,
                             ApplicationEventPublisher eventPublisher) {
        this.claimRepository = claimRepository;
        this.invoiceRepository = invoiceRepository;
        this.claimNumberGenerator = claimNumberGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ClaimResponse submitClaim(SubmitClaimRequest request, String createdBy) {
        log.info("Submitting insurance claim for invoice: {}", request.invoiceId());

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
            .orElseThrow(() -> new InvoiceNotFoundException(request.invoiceId()));

        if (claimRepository.findByInvoiceId(invoice.getId()).isPresent()) {
            throw new ClaimProcessingException(
                "Insurance claim already exists for invoice: " + invoice.getInvoiceNumber());
        }

        InsuranceClaim claim = InsuranceClaim.builder()
            .claimNumber(claimNumberGenerator.generate())
            .invoiceId(invoice.getId())
            .patientId(invoice.getPatientId())
            .insuranceProvider(request.insuranceProvider())
            .policyNumber(request.policyNumber())
            .groupNumber(request.groupNumber())
            .subscriberName(request.subscriberName())
            .subscriberId(request.subscriberId())
            .billedAmount(request.billedAmount())
            .serviceDate(request.serviceDate())
            .createdBy(createdBy)
            .build();

        claim.submit();

        InsuranceClaim savedClaim = claimRepository.save(claim);

        invoice.recordInsuranceClaimNumber(savedClaim.getClaimNumber());
        invoiceRepository.save(invoice);

        log.info("Submitted claim {} for invoice {}", savedClaim.getClaimNumber(), invoice.getInvoiceNumber());

        eventPublisher.publishEvent(new ClaimSubmittedEvent(
            savedClaim.getId(),
            savedClaim.getClaimNumber(),
            savedClaim.getInvoiceId(),
            savedClaim.getPatientId(),
            savedClaim.getInsuranceProvider(),
            savedClaim.getBilledAmount(),
            createdBy
        ));

        return ClaimResponse.from(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaim(UUID claimId) {
        InsuranceClaim claim = findClaimById(claimId);
        return ClaimResponse.from(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimByNumber(String claimNumber) {
        InsuranceClaim claim = claimRepository.findByClaimNumber(claimNumber)
            .orElseThrow(() -> new ClaimNotFoundException(claimNumber));
        return ClaimResponse.from(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimForInvoice(UUID invoiceId) {
        InsuranceClaim claim = claimRepository.findByInvoiceId(invoiceId)
            .orElseThrow(() -> new ClaimNotFoundException(
                "No claim found for invoice: " + invoiceId));
        return ClaimResponse.from(claim);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> getPatientClaims(UUID patientId, Pageable pageable) {
        return claimRepository.findByPatientId(patientId, pageable)
            .map(ClaimResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> getClaimsByStatus(ClaimStatus status, Pageable pageable) {
        return claimRepository.findByStatus(status, pageable)
            .map(ClaimResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> getPendingClaims(Pageable pageable) {
        List<ClaimStatus> pendingStatuses = List.of(
            ClaimStatus.SUBMITTED,
            ClaimStatus.ACKNOWLEDGED,
            ClaimStatus.IN_REVIEW,
            ClaimStatus.PENDING_INFO
        );
        return claimRepository.findByStatuses(pendingStatuses, pageable)
            .map(ClaimResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClaimResponse> getClaimsByProvider(String provider, Pageable pageable) {
        return claimRepository.findByInsuranceProvider(provider, pageable)
            .map(ClaimResponse::from);
    }

    @Override
    public ClaimResponse processClaim(UUID claimId, ProcessClaimRequest request, String processedBy) {
        log.info("Processing claim {} with action: {}", claimId, request.action());

        InsuranceClaim claim = findClaimById(claimId);

        switch (request.action().toUpperCase()) {
            case ProcessClaimRequest.ACTION_APPROVE -> {
                claim.approve(
                    request.allowedAmount(),
                    request.paidAmount(),
                    request.patientResponsibility()
                );
                if (request.copayAmount() != null || request.deductibleAmount() != null || request.coinsuranceAmount() != null) {
                    claim.recordPatientBreakdown(
                        request.copayAmount() != null ? request.copayAmount() : java.math.BigDecimal.ZERO,
                        request.deductibleAmount() != null ? request.deductibleAmount() : java.math.BigDecimal.ZERO,
                        request.coinsuranceAmount() != null ? request.coinsuranceAmount() : java.math.BigDecimal.ZERO
                    );
                }
                updateInvoiceWithClaimApproval(claim);
            }
            case ProcessClaimRequest.ACTION_PARTIALLY_APPROVE -> {
                claim.partiallyApprove(
                    request.allowedAmount(),
                    request.paidAmount(),
                    request.patientResponsibility(),
                    request.notes()
                );
                if (request.copayAmount() != null || request.deductibleAmount() != null || request.coinsuranceAmount() != null) {
                    claim.recordPatientBreakdown(
                        request.copayAmount() != null ? request.copayAmount() : java.math.BigDecimal.ZERO,
                        request.deductibleAmount() != null ? request.deductibleAmount() : java.math.BigDecimal.ZERO,
                        request.coinsuranceAmount() != null ? request.coinsuranceAmount() : java.math.BigDecimal.ZERO
                    );
                }
                updateInvoiceWithClaimApproval(claim);
            }
            case ProcessClaimRequest.ACTION_DENY -> {
                claim.deny(request.denialCode(), request.denialReason());
            }
            case ProcessClaimRequest.ACTION_REQUEST_INFO -> {
                claim.requestInfo(request.notes());
            }
            default -> throw new ClaimProcessingException("Unknown claim action: " + request.action());
        }

        InsuranceClaim savedClaim = claimRepository.save(claim);
        log.info("Processed claim {} with action {}", savedClaim.getClaimNumber(), request.action());

        eventPublisher.publishEvent(new ClaimStatusChangedEvent(
            savedClaim.getId(),
            savedClaim.getClaimNumber(),
            claim.getStatus(),
            savedClaim.getStatus(),
            savedClaim.getPaidAmount() != null ? savedClaim.getPaidAmount() : java.math.BigDecimal.ZERO,
            savedClaim.getPatientResponsibility() != null ? savedClaim.getPatientResponsibility() : java.math.BigDecimal.ZERO,
            request.notes(),
            processedBy
        ));

        return ClaimResponse.from(savedClaim);
    }

    @Override
    public ClaimResponse appealClaim(UUID claimId, String appealNotes, String processedBy) {
        InsuranceClaim claim = findClaimById(claimId);

        if (!claim.getStatus().canAppeal()) {
            throw new ClaimProcessingException(claimId, claim.getClaimNumber(), claim.getStatus(),
                "Cannot appeal claim in status: " + claim.getStatus());
        }

        claim.appeal(appealNotes);

        InsuranceClaim savedClaim = claimRepository.save(claim);
        log.info("Appealed claim {}", savedClaim.getClaimNumber());

        return ClaimResponse.from(savedClaim);
    }

    @Override
    public ClaimResponse markClaimPaid(UUID claimId, String eobReference, String processedBy) {
        InsuranceClaim claim = findClaimById(claimId);
        claim.markPaid(eobReference);

        InsuranceClaim savedClaim = claimRepository.save(claim);
        log.info("Marked claim {} as paid", savedClaim.getClaimNumber());

        return ClaimResponse.from(savedClaim);
    }

    @Override
    public ClaimResponse closeClaim(UUID claimId, String processedBy) {
        InsuranceClaim claim = findClaimById(claimId);
        claim.close();

        InsuranceClaim savedClaim = claimRepository.save(claim);
        log.info("Closed claim {}", savedClaim.getClaimNumber());

        return ClaimResponse.from(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimResponse> getPendingClaimsForPatient(UUID patientId) {
        return claimRepository.findPendingClaimsForPatient(patientId)
            .stream()
            .map(ClaimResponse::from)
            .toList();
    }

    private InsuranceClaim findClaimById(UUID claimId) {
        return claimRepository.findById(claimId)
            .orElseThrow(() -> new ClaimNotFoundException(claimId));
    }

    private void updateInvoiceWithClaimApproval(InsuranceClaim claim) {
        Invoice invoice = invoiceRepository.findById(claim.getInvoiceId())
            .orElseThrow(() -> new InvoiceNotFoundException(claim.getInvoiceId()));

        if (claim.getPaidAmount() != null && claim.getPaidAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
            invoice.recordInsurancePayment(claim.getPaidAmount());
            invoiceRepository.save(invoice);
        }
    }
}
