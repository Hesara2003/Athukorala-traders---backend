package com.example.hardwaremanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryReportDTO {
    private LocalDateTime reportDate;
    private int totalProducts;
    private int totalStockValue;
    private int lowStockCount;
    private int outOfStockCount;
    private List<InventoryItemDTO> inventoryItems;
    private List<InventoryItemDTO> lowStockItems;
    private List<InventoryItemDTO> outOfStockItems;

    // Constructors
    public InventoryReportDTO() {}

    public InventoryReportDTO(LocalDateTime reportDate, int totalProducts, int totalStockValue) {
        this.reportDate = reportDate;
        this.totalProducts = totalProducts;
        this.totalStockValue = totalStockValue;
    }

    // Getters and Setters
    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalStockValue() {
        return totalStockValue;
    }

    public void setTotalStockValue(int totalStockValue) {
        this.totalStockValue = totalStockValue;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(int outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public List<InventoryItemDTO> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItemDTO> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public List<InventoryItemDTO> getLowStockItems() {
        return lowStockItems;
    }

    public void setLowStockItems(List<InventoryItemDTO> lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    public List<InventoryItemDTO> getOutOfStockItems() {
        return outOfStockItems;
    }

    public void setOutOfStockItems(List<InventoryItemDTO> outOfStockItems) {
        this.outOfStockItems = outOfStockItems;
    }
}
