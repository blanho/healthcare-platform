package com.healthcare.billing.service.impl;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.*;
import com.healthcare.billing.domain.event.PaymentReceivedEvent;
import com.healthcare.billing.exception.InvoiceNotFoundException;
import com.healthcare.billing.exception.PaymentNotFoundException;
import com.healthcare.billing.exception.PaymentProcessingException;
import com.healthcare.billing.repository.InvoiceRepository;
import com.healthcare.billing.repository.PaymentRepository;
import com.healthcare.billing.service.PaymentReferenceGenerator;
import com.healthcare.billing.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentReferenceGenerator referenceGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               InvoiceRepository invoiceRepository,
                               PaymentReferenceGenerator referenceGenerator,
                               ApplicationEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.referenceGenerator = referenceGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PaymentResponse recordPayment(RecordPaymentRequest request, String createdBy) {
        log.info("Recording payment for invoice: {}", request.invoiceId());

        Invoice invoice = invoiceRepository.findById(request.invoiceId())
            .orElseThrow(() -> new InvoiceNotFoundException(request.invoiceId()));

        if (!invoice.getStatus().canAcceptPayment()) {
            throw new PaymentProcessingException(
                "Invoice " + invoice.getInvoiceNumber() + " cannot accept payments in status: " + invoice.getStatus());
        }

        if (request.amount().compareTo(invoice.getBalanceDue()) > 0) {
            throw new PaymentProcessingException(
                "Payment amount exceeds balance due. Balance: " + invoice.getBalanceDue());
        }

        Payment payment = Payment.builder()
            .referenceNumber(referenceGenerator.generate())
            .invoiceId(invoice.getId())
            .patientId(invoice.getPatientId())
            .amount(request.amount())
            .paymentMethod(request.paymentMethod())
            .notes(request.notes())
            .createdBy(createdBy)
            .build();

        if (request.cardLastFour() != null) {
            payment.recordCardDetails(request.cardLastFour(), request.cardBrand());
        }

        payment.complete(
            "TXN-" + System.currentTimeMillis(),
            "AUTH-" + System.currentTimeMillis()
        );

        Payment savedPayment = paymentRepository.save(payment);

        invoice.recordPayment(request.amount());
        invoiceRepository.save(invoice);

        log.info("Recorded payment {} for invoice {}", savedPayment.getReferenceNumber(), invoice.getInvoiceNumber());

        eventPublisher.publishEvent(new PaymentReceivedEvent(
            savedPayment.getId(),
            savedPayment.getReferenceNumber(),
            invoice.getId(),
            savedPayment.getPatientId(),
            savedPayment.getAmount(),
            savedPayment.getPaymentMethod(),
            invoice.getBalanceDue(),
            createdBy
        ));

        return PaymentResponse.from(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String referenceNumber) {
        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new PaymentNotFoundException(referenceNumber));
        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getInvoicePayments(UUID invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId)
            .stream()
            .map(PaymentResponse::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPatientPayments(UUID patientId, Pageable pageable) {
        return paymentRepository.findByPatientId(patientId, pageable)
            .map(PaymentResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable)
            .map(PaymentResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsByMethod(PaymentMethod method, Pageable pageable) {
        return paymentRepository.findByPaymentMethod(method, pageable)
            .map(PaymentResponse::from);
    }

    @Override
    public PaymentResponse refundPayment(UUID paymentId, BigDecimal amount, String processedBy) {
        Payment payment = findPaymentById(paymentId);

        if (!payment.getStatus().canRefund()) {
            throw new PaymentProcessingException(
                "Cannot refund payment in status: " + payment.getStatus());
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new PaymentProcessingException(
                "Refund amount exceeds payment amount");
        }

        payment.refund(amount);
        Payment savedPayment = paymentRepository.save(payment);

        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
            .orElseThrow(() -> new InvoiceNotFoundException(payment.getInvoiceId()));
        invoice.refund(amount);
        invoiceRepository.save(invoice);

        log.info("Refunded {} from payment {}", amount, payment.getReferenceNumber());

        return PaymentResponse.from(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateRevenueInPeriod(Instant startDate, Instant endDate) {
        return paymentRepository.calculateRevenueInPeriod(startDate, endDate)
            .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPatientPaymentHistory(UUID patientId) {
        return paymentRepository.findPatientPaymentHistory(patientId)
            .stream()
            .map(PaymentResponse::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPaymentsInDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        return paymentRepository.findPaymentsInDateRange(startDate, endDate, pageable)
            .map(PaymentResponse::from);
    }

    private Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}
