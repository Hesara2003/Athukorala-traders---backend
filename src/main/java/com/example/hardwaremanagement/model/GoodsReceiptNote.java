package com.example.hardwaremanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * GRN (Goods Receipt Note) Model
 * Records the receipt of goods against a Purchase Order
 */
@Document(collection = "goods_receipt_notes")
public class GoodsReceiptNote {
    @Id
    private String id;
    private String purchaseOrderId;
    private String supplierId;
    private String receivedBy; // Staff member who received the goods
    private LocalDateTime receivedDate;
    private List<GRNItem> items;
    private String notes; // Additional notes about the receipt
    private String status; // COMPLETED, PARTIAL, DISCREPANCY
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public GoodsReceiptNote() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.receivedDate = LocalDateTime.now();
        this.status = "COMPLETED";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public List<GRNItem> getItems() {
        return items;
    }

    public void setItems(List<GRNItem> items) {
        this.items = items;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
