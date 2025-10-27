package com.example.hardwaremanagement.dto;


public class UpdateOrderStatusRequest {
    private String status; // PROCESSING, SHIPPED, DELIVERED, CANCELLED


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}




