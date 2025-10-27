package com.example.hardwaremanagement.dto;

import java.time.LocalDate;

public class DailySalesDTO {
    private LocalDate date;
    private double totalRevenue;
    private int totalOrders;
    private int totalItems;

    // Constructors
    public DailySalesDTO() {}

    public DailySalesDTO(LocalDate date, double totalRevenue, int totalOrders, int totalItems) {
        this.date = date;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalItems = totalItems;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
