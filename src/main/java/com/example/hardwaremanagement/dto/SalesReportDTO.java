package com.example.hardwaremanagement.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SalesReportDTO {
    private LocalDateTime reportDate;
    private double totalSales;
    private double totalRevenue;
    private int totalOrders;
    private int totalProducts;
    private List<ProductSalesDTO> topSellingProducts;
    private List<DailySalesDTO> dailySales;

    // Constructors
    public SalesReportDTO() {}

    public SalesReportDTO(LocalDateTime reportDate, double totalRevenue, int totalOrders, int totalProducts) {
        this.reportDate = reportDate;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
    }

    // Getters and Setters
    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public List<ProductSalesDTO> getTopSellingProducts() {
        return topSellingProducts;
    }

    public void setTopSellingProducts(List<ProductSalesDTO> topSellingProducts) {
        this.topSellingProducts = topSellingProducts;
    }

    public List<DailySalesDTO> getDailySales() {
        return dailySales;
    }

    public void setDailySales(List<DailySalesDTO> dailySales) {
        this.dailySales = dailySales;
    }
}
