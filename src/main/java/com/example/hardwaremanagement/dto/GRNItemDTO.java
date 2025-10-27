package com.example.hardwaremanagement.dto;

/**
 * DTO for GRN Item
 * Used for API requests and responses
 */
public class GRNItemDTO {
    private String productId;
    private String productName;
    private String productSku;
    private int orderedQuantity;
    private int receivedQuantity;
    private String condition; // GOOD, DAMAGED, DEFECTIVE
    private String remarks;

    // Constructors
    public GRNItemDTO() {
    }

    public GRNItemDTO(String productId, String productName, int orderedQuantity, int receivedQuantity) {
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

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
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
