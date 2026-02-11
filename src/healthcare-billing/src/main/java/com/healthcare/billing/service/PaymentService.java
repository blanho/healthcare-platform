package com.healthcare.billing.service;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.PaymentMethod;
import com.healthcare.billing.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse recordPayment(RecordPaymentRequest request, String createdBy);

    PaymentResponse getPayment(UUID paymentId);

    PaymentResponse getPaymentByReference(String referenceNumber);

    List<PaymentResponse> getInvoicePayments(UUID invoiceId);

    Page<PaymentResponse> getPatientPayments(UUID patientId, Pageable pageable);

    Page<PaymentResponse> getPaymentsByStatus(PaymentStatus status, Pageable pageable);

    Page<PaymentResponse> getPaymentsByMethod(PaymentMethod method, Pageable pageable);

    PaymentResponse refundPayment(UUID paymentId, BigDecimal amount, String processedBy);

    BigDecimal calculateRevenueInPeriod(Instant startDate, Instant endDate);

    List<PaymentResponse> getPatientPaymentHistory(UUID patientId);

    Page<PaymentResponse> getPaymentsInDateRange(Instant startDate, Instant endDate, Pageable pageable);
}
