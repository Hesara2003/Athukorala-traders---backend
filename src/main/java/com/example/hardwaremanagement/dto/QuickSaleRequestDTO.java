package com.example.hardwaremanagement.dto;

import java.util.List;

public class QuickSaleRequestDTO {
    private List<QuickSaleItemDTO> items;
    private String staffId;

    public List<QuickSaleItemDTO> getItems() {
        return items;
    }

    public void setItems(List<QuickSaleItemDTO> items) {
        this.items = items;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}