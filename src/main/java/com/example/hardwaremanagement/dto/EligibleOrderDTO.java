package com.example.hardwaremanagement.dto;

import com.example.hardwaremanagement.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class EligibleOrderDTO {
    private String orderId;
    private String customerEmail;
    private LocalDateTime orderDate;
    private double totalAmount;
    private OrderStatus status;
    private List<OrderItemDTO> items;
    private boolean eligibleForReturn;
    private String eligibilityReason;
    private int daysUntilReturnExpires;

    // Constructors
    public EligibleOrderDTO() {
    }

    public EligibleOrderDTO(String orderId, String customerEmail, LocalDateTime orderDate, 
                           double totalAmount, OrderStatus status, List<OrderItemDTO> items,
                           boolean eligibleForReturn, String eligibilityReason, int daysUntilReturnExpires) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.items = items;
        this.eligibleForReturn = eligibleForReturn;
        this.eligibilityReason = eligibilityReason;
        this.daysUntilReturnExpires = daysUntilReturnExpires;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public boolean isEligibleForReturn() {
        return eligibleForReturn;
    }

    public void setEligibleForReturn(boolean eligibleForReturn) {
        this.eligibleForReturn = eligibleForReturn;
    }

    public String getEligibilityReason() {
        return eligibilityReason;
    }

    public void setEligibilityReason(String eligibilityReason) {
        this.eligibilityReason = eligibilityReason;
    }

    public int getDaysUntilReturnExpires() {
        return daysUntilReturnExpires;
    }

    public void setDaysUntilReturnExpires(int daysUntilReturnExpires) {
        this.daysUntilReturnExpires = daysUntilReturnExpires;
    }

    // Inner class for order items
    public static class OrderItemDTO {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
        private double total;
        private boolean returnable;
        private String returnReason;

        // Constructors
        public OrderItemDTO() {
        }

        public OrderItemDTO(String productId, String productName, int quantity, 
                           double price, double total, boolean returnable, String returnReason) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
            this.returnable = returnable;
            this.returnReason = returnReason;
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

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public boolean isReturnable() {
            return returnable;
        }

        public void setReturnable(boolean returnable) {
            this.returnable = returnable;
        }

        public String getReturnReason() {
            return returnReason;
        }

        public void setReturnReason(String returnReason) {
            this.returnReason = returnReason;
        }
    }
}
