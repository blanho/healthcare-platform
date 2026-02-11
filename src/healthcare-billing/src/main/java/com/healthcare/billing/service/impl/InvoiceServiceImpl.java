package com.healthcare.billing.service.impl;

import com.healthcare.billing.api.dto.*;
import com.healthcare.billing.domain.*;
import com.healthcare.billing.domain.event.InvoiceCreatedEvent;
import com.healthcare.billing.domain.event.InvoiceFinalizedEvent;
import com.healthcare.billing.domain.event.InvoiceOverdueEvent;
import com.healthcare.billing.exception.InvoiceNotFoundException;
import com.healthcare.billing.exception.InvalidInvoiceOperationException;
import com.healthcare.billing.repository.InvoiceRepository;
import com.healthcare.billing.service.InvoiceNumberGenerator;
import com.healthcare.billing.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                               InvoiceNumberGenerator invoiceNumberGenerator,
                               ApplicationEventPublisher eventPublisher) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceNumberGenerator = invoiceNumberGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest request, String createdBy) {
        log.info("Creating invoice for patient: {}", request.patientId());

        Invoice invoice = Invoice.builder()
            .invoiceNumber(invoiceNumberGenerator.generate())
            .patientId(request.patientId())
            .appointmentId(request.appointmentId())
            .dueDate(request.dueDate())
            .notes(request.notes())
            .createdBy(createdBy)
            .build();

        for (CreateInvoiceRequest.InvoiceItemRequest itemRequest : request.items()) {
            InvoiceItem item = InvoiceItem.forService(
                itemRequest.description(),
                itemRequest.procedureCode(),
                itemRequest.quantity(),
                itemRequest.unitPrice()
            );
            invoice.addItem(item);
        }

        if (request.taxRate() != null && request.taxRate().compareTo(BigDecimal.ZERO) > 0) {
            invoice.applyTax(request.taxRate());
        }

        if (request.discountAmount() != null && request.discountAmount().compareTo(BigDecimal.ZERO) > 0) {
            invoice.applyDiscount(request.discountAmount());
        } else if (request.discountPercentage() != null && request.discountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            invoice.applyPercentageDiscount(request.discountPercentage());
        }

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Created invoice: {}", saved.getInvoiceNumber());

        eventPublisher.publishEvent(new InvoiceCreatedEvent(
            saved.getId(),
            saved.getInvoiceNumber(),
            saved.getPatientId(),
            saved.getTotalAmount(),
            createdBy
        ));

        return InvoiceResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(UUID invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);
        return InvoiceResponse.from(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceNumber));
        return InvoiceResponse.from(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getPatientInvoices(UUID patientId, Pageable pageable) {
        return invoiceRepository.findByPatientId(patientId, pageable)
            .map(InvoiceSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getInvoicesByStatus(InvoiceStatus status, Pageable pageable) {
        return invoiceRepository.findByStatus(status, pageable)
            .map(InvoiceSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceSummaryResponse> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(InvoiceStatus.PENDING, LocalDate.now())
            .stream()
            .map(InvoiceSummaryResponse::from)
            .toList();
    }

    @Override
    public InvoiceResponse addItem(UUID invoiceId, AddInvoiceItemRequest request, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "addItem");
        }

        InvoiceItem item = InvoiceItem.forService(
            request.description(),
            request.procedureCode(),
            request.quantity(),
            request.unitPrice()
        );
        invoice.addItem(item);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Added item to invoice: {}", saved.getInvoiceNumber());

        return InvoiceResponse.from(saved);
    }

    @Override
    public InvoiceResponse removeItem(UUID invoiceId, UUID itemId, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "removeItem");
        }

        invoice.removeItem(itemId);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Removed item {} from invoice: {}", itemId, saved.getInvoiceNumber());

        return InvoiceResponse.from(saved);
    }

    @Override
    public InvoiceResponse applyDiscount(UUID invoiceId, BigDecimal amount, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "applyDiscount");
        }

        invoice.applyDiscount(amount);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Applied discount {} to invoice: {}", amount, saved.getInvoiceNumber());

        return InvoiceResponse.from(saved);
    }

    @Override
    public InvoiceResponse applyPercentageDiscount(UUID invoiceId, BigDecimal percentage, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "applyPercentageDiscount");
        }

        invoice.applyPercentageDiscount(percentage);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Applied {}% discount to invoice: {}", percentage, saved.getInvoiceNumber());

        return InvoiceResponse.from(saved);
    }

    @Override
    public InvoiceResponse finalizeInvoice(UUID invoiceId, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "finalize");
        }

        if (invoice.getItems().isEmpty()) {
            throw new InvalidInvoiceOperationException("Cannot finalize invoice with no items");
        }

        invoice.finalize();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Finalized invoice: {}", saved.getInvoiceNumber());

        eventPublisher.publishEvent(new InvoiceFinalizedEvent(
            saved.getId(),
            saved.getInvoiceNumber(),
            saved.getPatientId(),
            saved.getTotalAmount(),
            saved.getBalanceDue(),
            updatedBy
        ));

        return InvoiceResponse.from(saved);
    }

    @Override
    public InvoiceResponse cancelInvoice(UUID invoiceId, String updatedBy) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (!invoice.getStatus().canCancel()) {
            throw new InvalidInvoiceOperationException(invoiceId, invoice.getStatus(), "cancel");
        }

        invoice.cancel();

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Cancelled invoice: {}", saved.getInvoiceNumber());

        return InvoiceResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPatientBalance(UUID patientId) {
        return invoiceRepository.calculatePatientBalance(patientId)
            .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceForAppointment(UUID appointmentId) {
        Invoice invoice = invoiceRepository.findByAppointmentId(appointmentId)
            .orElseThrow(() -> new InvoiceNotFoundException(
                "Invoice not found for appointment: " + appointmentId));
        return InvoiceResponse.from(invoice);
    }

    @Override
    public int markOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(
            InvoiceStatus.PENDING, LocalDate.now());

        int count = 0;
        for (Invoice invoice : overdueInvoices) {
            try {
                invoice.markOverdue();
                invoiceRepository.save(invoice);
                count++;
                log.info("Marked invoice {} as overdue", invoice.getInvoiceNumber());

                int daysOverdue = (int) java.time.temporal.ChronoUnit.DAYS.between(
                    invoice.getDueDate(), LocalDate.now());

                eventPublisher.publishEvent(new InvoiceOverdueEvent(
                    invoice.getId(),
                    invoice.getInvoiceNumber(),
                    invoice.getPatientId(),
                    invoice.getBalanceDue(),
                    daysOverdue,
                    "SYSTEM"
                ));
            } catch (Exception e) {
                log.error("Failed to mark invoice {} as overdue", invoice.getInvoiceNumber(), e);
            }
        }

        log.info("Marked {} invoices as overdue", count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> getInvoicesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return invoiceRepository.findByInvoiceDateRange(startDate, endDate, pageable)
            .map(InvoiceSummaryResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = invoiceRepository.sumRevenueForPeriod(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCollectionsForPeriod(LocalDate startDate, LocalDate endDate) {
        BigDecimal collections = invoiceRepository.sumCollectionsForPeriod(startDate, endDate);
        return collections != null ? collections : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOutstandingForPeriod(LocalDate startDate, LocalDate endDate) {
        BigDecimal outstanding = invoiceRepository.sumOutstandingForPeriod(startDate, endDate);
        return outstanding != null ? outstanding : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstanding() {
        BigDecimal outstanding = invoiceRepository.sumTotalOutstanding();
        return outstanding != null ? outstanding : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(InvoiceStatus status) {
        return invoiceRepository.countByStatus(status);
    }

    private Invoice findInvoiceById(UUID invoiceId) {
        return invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
    }
}
