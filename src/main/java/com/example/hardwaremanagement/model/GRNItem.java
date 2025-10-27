package com.example.hardwaremanagement.model;

/**
 * GRN Item Model
 * Represents individual items received in a GRN
 */
public class GRNItem {
    private String productId;
    private String productName; // Denormalized for easier reporting
    private int orderedQuantity; // Quantity that was ordered
    private int receivedQuantity; // Quantity actually received
    private String condition; // GOOD, DAMAGED, DEFECTIVE
    private String remarks; // Notes about the item condition

    // Constructors
    public GRNItem() {
    }

    public GRNItem(String productId, String productName, int orderedQuantity, int receivedQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
        this.condition = "GOOD";
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(int orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
