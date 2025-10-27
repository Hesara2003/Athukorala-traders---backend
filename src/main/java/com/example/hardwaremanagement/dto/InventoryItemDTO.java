package com.example.hardwaremanagement.dto;

public class InventoryItemDTO {
    private String productId;
    private String productName;
    private String sku;
    private String category;
    private String brand;
    private int currentStock;
    private double unitPrice;
    private double stockValue;
    private boolean isLowStock;
    private boolean isOutOfStock;
    private boolean isAvailable;

    // Constructors
    public InventoryItemDTO() {}

    public InventoryItemDTO(String productId, String productName, String sku, int currentStock, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.currentStock = currentStock;
        this.unitPrice = unitPrice;
        this.stockValue = currentStock * unitPrice;
        this.isOutOfStock = currentStock == 0;
        this.isLowStock = currentStock > 0 && currentStock <= 5; // Consider low stock as <= 5 items
        this.isAvailable = currentStock > 0;
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
        this.stockValue = currentStock * this.unitPrice;
        this.isOutOfStock = currentStock == 0;
        this.isLowStock = currentStock > 0 && currentStock <= 5;
        this.isAvailable = currentStock > 0;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.stockValue = this.currentStock * unitPrice;
    }

    public double getStockValue() {
        return stockValue;
    }

    public void setStockValue(double stockValue) {
        this.stockValue = stockValue;
    }

    public boolean isLowStock() {
        return isLowStock;
    }

    public void setLowStock(boolean lowStock) {
        isLowStock = lowStock;
    }

    public boolean isOutOfStock() {
        return isOutOfStock;
    }

    public void setOutOfStock(boolean outOfStock) {
        isOutOfStock = outOfStock;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
