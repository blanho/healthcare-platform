package com.healthcare.billing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_invoice_patient", columnList = "patient_id"),
    @Index(name = "idx_invoice_appointment", columnList = "appointment_id"),
    @Index(name = "idx_invoice_status", columnList = "status"),
    @Index(name = "idx_invoice_due_date", columnList = "due_date")
})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "balance_due", nullable = false, precision = 12, scale = 2)
    private BigDecimal balanceDue;

    @NotNull
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status;

    @Column(name = "insurance_claim_number", length = 100)
    private String insuranceClaimNumber;

    @Column(name = "insurance_amount", precision = 12, scale = 2)
    private BigDecimal insuranceAmount;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    @org.hibernate.annotations.BatchSize(size = 25)
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    protected Invoice() {

    }

    private Invoice(Builder builder) {
        this.invoiceNumber = builder.invoiceNumber;
        this.patientId = builder.patientId;
        this.appointmentId = builder.appointmentId;
        this.invoiceDate = builder.invoiceDate;
        this.dueDate = builder.dueDate;
        this.notes = builder.notes;
        this.status = InvoiceStatus.DRAFT;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.paidAmount = BigDecimal.ZERO;
        this.balanceDue = BigDecimal.ZERO;
        this.insuranceAmount = BigDecimal.ZERO;
        this.deleted = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.createdBy = builder.createdBy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void addItem(InvoiceItem item) {
        if (!status.equals(InvoiceStatus.DRAFT)) {
            throw new IllegalStateException("Cannot modify finalized invoice");
        }
        item.assignToInvoice(this.id);
        this.items.add(item);
        recalculateTotals();
    }

    public void removeItem(UUID itemId) {
        if (!status.equals(InvoiceStatus.DRAFT)) {
            throw new IllegalStateException("Cannot modify finalized invoice");
        }
        items.removeIf(item -> item.getId().equals(itemId));
        recalculateTotals();
    }

    public void finalize() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Invoice already finalized");
        }
        if (this.items.isEmpty()) {
            throw new IllegalStateException("Cannot finalize invoice without items");
        }
        recalculateTotals();
        this.status = InvoiceStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void applyDiscount(BigDecimal discount) {
        if (!status.equals(InvoiceStatus.DRAFT)) {
            throw new IllegalStateException("Cannot modify finalized invoice");
        }
        this.discountAmount = discount;
        recalculateTotals();
    }

    public void applyPercentageDiscount(BigDecimal percentage) {
        if (!status.equals(InvoiceStatus.DRAFT)) {
            throw new IllegalStateException("Cannot modify finalized invoice");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountAmount = this.subtotal.multiply(percentage).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        recalculateTotals();
    }

    public void applyTax(BigDecimal taxRate) {
        if (!status.equals(InvoiceStatus.DRAFT)) {
            throw new IllegalStateException("Cannot modify finalized invoice");
        }
        this.taxAmount = this.subtotal.subtract(this.discountAmount)
            .multiply(taxRate.divide(BigDecimal.valueOf(100)));
        recalculateTotals();
    }

    public void recordPayment(BigDecimal amount) {
        if (!status.canAcceptPayment()) {
            throw new IllegalStateException("Invoice cannot accept payments in status: " + status);
        }

        this.paidAmount = this.paidAmount.add(amount);
        this.balanceDue = this.totalAmount.subtract(this.paidAmount);

        if (this.balanceDue.compareTo(BigDecimal.ZERO) <= 0) {
            this.status = InvoiceStatus.PAID;
            this.paidDate = LocalDate.now();
            this.balanceDue = BigDecimal.ZERO;
        } else {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }

        this.updatedAt = Instant.now();
    }

    public void recordInsuranceClaimNumber(String claimNumber) {
        this.insuranceClaimNumber = claimNumber;
        this.updatedAt = Instant.now();
    }

    public void recordInsurancePayment(BigDecimal amount) {
        this.insuranceAmount = this.insuranceAmount.add(amount);
        recordPayment(amount);
    }

    public void cancel() {
        if (!status.canCancel()) {
            throw new IllegalStateException("Cannot cancel invoice in status: " + status);
        }
        this.status = InvoiceStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void refund(BigDecimal amount) {
        if (!status.canRefund()) {
            throw new IllegalStateException("Cannot refund invoice in status: " + status);
        }
        if (amount.compareTo(this.paidAmount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds paid amount");
        }

        this.paidAmount = this.paidAmount.subtract(amount);
        this.balanceDue = this.totalAmount.subtract(this.paidAmount);

        if (this.paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = InvoiceStatus.REFUNDED;
        } else {
            this.status = InvoiceStatus.PARTIALLY_PAID;
        }

        this.updatedAt = Instant.now();
    }

    public void writeOff() {
        if (this.balanceDue.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("No balance to write off");
        }
        this.status = InvoiceStatus.WRITE_OFF;
        this.balanceDue = BigDecimal.ZERO;
        this.updatedAt = Instant.now();
    }

    public void markOverdue() {
        if (this.status == InvoiceStatus.PENDING || this.status == InvoiceStatus.PARTIALLY_PAID) {
            if (this.dueDate.isBefore(LocalDate.now())) {
                this.status = InvoiceStatus.OVERDUE;
                this.updatedAt = Instant.now();
            }
        }
    }

    private void recalculateTotals() {
        this.subtotal = items.stream()
            .map(InvoiceItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discounted = this.subtotal.subtract(this.discountAmount != null ? this.discountAmount : BigDecimal.ZERO);
        BigDecimal tax = this.taxAmount != null ? this.taxAmount : BigDecimal.ZERO;

        this.totalAmount = discounted.add(tax);
        this.balanceDue = this.totalAmount.subtract(this.paidAmount != null ? this.paidAmount : BigDecimal.ZERO);
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public UUID getPatientId() { return patientId; }
    public UUID getAppointmentId() { return appointmentId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getBalanceDue() { return balanceDue; }
    public LocalDate getInvoiceDate() { return invoiceDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getPaidDate() { return paidDate; }
    public InvoiceStatus getStatus() { return status; }
    public String getInsuranceClaimNumber() { return insuranceClaimNumber; }
    public BigDecimal getInsuranceAmount() { return insuranceAmount; }
    public String getNotes() { return notes; }
    public List<InvoiceItem> getItems() { return Collections.unmodifiableList(items); }
    public boolean isDeleted() { return deleted; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public Long getVersion() { return version; }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = Instant.now();
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    public static class Builder {
        private String invoiceNumber;
        private UUID patientId;
        private UUID appointmentId;
        private LocalDate invoiceDate;
        private LocalDate dueDate;
        private BigDecimal taxRate;
        private String notes;
        private String createdBy;

        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder appointmentId(UUID appointmentId) {
            this.appointmentId = appointmentId;
            return this;
        }

        public Builder invoiceDate(LocalDate invoiceDate) {
            this.invoiceDate = invoiceDate;
            return this;
        }

        public Builder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder taxRate(BigDecimal taxRate) {
            this.taxRate = taxRate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public BigDecimal getTaxRate() {
            return taxRate;
        }

        public Invoice build() {
            if (invoiceNumber == null || invoiceNumber.isBlank()) {
                throw new IllegalStateException("Invoice number is required");
            }
            if (patientId == null) {
                throw new IllegalStateException("Patient ID is required");
            }
            if (invoiceDate == null) {
                invoiceDate = LocalDate.now();
            }
            if (dueDate == null) {
                dueDate = invoiceDate.plusDays(30);
            }
            return new Invoice(this);
        }
    }
}
