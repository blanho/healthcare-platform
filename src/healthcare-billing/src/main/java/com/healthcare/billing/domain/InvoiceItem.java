package com.healthcare.billing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invoice_items", indexes = {
    @Index(name = "idx_invoice_item_invoice", columnList = "invoice_id")
})
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @NotBlank
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "procedure_code", length = 20)
    private String procedureCode;

    @Positive
    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected InvoiceItem() {

    }

    public InvoiceItem(String description, String procedureCode, int quantity, BigDecimal unitPrice) {
        this.description = description;
        this.procedureCode = procedureCode;
        this.quantity = quantity > 0 ? quantity : 1;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        this.createdAt = Instant.now();
    }

    public static InvoiceItem forService(String description, String cptCode, BigDecimal price) {
        return new InvoiceItem(description, cptCode, 1, price);
    }

    public static InvoiceItem forService(String description, String cptCode, int quantity, BigDecimal unitPrice) {
        return new InvoiceItem(description, cptCode, quantity, unitPrice);
    }

    public static InvoiceItem forItem(String description, int quantity, BigDecimal unitPrice) {
        return new InvoiceItem(description, null, quantity, unitPrice);
    }

    void assignToInvoice(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void recalculateTotal() {
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = newQuantity;
        recalculateTotal();
    }

    public UUID getId() { return id; }
    public UUID getInvoiceId() { return invoiceId; }
    public String getDescription() { return description; }
    public String getProcedureCode() { return procedureCode; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Instant getCreatedAt() { return createdAt; }
}
