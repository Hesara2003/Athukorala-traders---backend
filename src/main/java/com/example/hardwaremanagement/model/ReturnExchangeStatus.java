package com.example.hardwaremanagement.model;

public enum ReturnExchangeStatus {
    PENDING,       // Initial request
    APPROVED,      // Staff approved the return/exchange
    REJECTED,      // Staff rejected the request
    IN_TRANSIT,    // Item being returned to warehouse
    RECEIVED,      // Item received at warehouse
    INSPECTING,    // Staff inspecting the returned item
    COMPLETED,     // Return/exchange completed, refund issued
    CANCELLED      // Customer cancelled the request
}
