package com.example.hardwaremanagement.dto;

import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDetailDTO {
    private String id;
    private String customerId;
    private String customerReference;
    private LocalDateTime placedAt;
    private OrderStatus status;
    private double totalAmount;
    private List<OrderItemDTO> items;
    private String customerEmail;
    private Order.Totals totals;
    private Order.BillingInfo billing;
    private Order.ShippingInfo shipping;
    private boolean shippingSameAsBilling;
    private Order.PaymentSummary payment;

    public OrderDetailDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(LocalDateTime placedAt) {
        this.placedAt = placedAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Order.Totals getTotals() {
        return totals;
    }

    public void setTotals(Order.Totals totals) {
        this.totals = totals;
    }

    public Order.BillingInfo getBilling() {
        return billing;
    }

    public void setBilling(Order.BillingInfo billing) {
        this.billing = billing;
    }

    public Order.ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(Order.ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public boolean isShippingSameAsBilling() {
        return shippingSameAsBilling;
    }

    public void setShippingSameAsBilling(boolean shippingSameAsBilling) {
        this.shippingSameAsBilling = shippingSameAsBilling;
    }

    public Order.PaymentSummary getPayment() {
        return payment;
    }

    public void setPayment(Order.PaymentSummary payment) {
        this.payment = payment;
    }
}
