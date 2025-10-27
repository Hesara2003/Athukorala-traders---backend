package com.example.hardwaremanagement.dto;

import java.time.LocalDateTime;

public class CheckoutDetailsResponse {
    private String id;
    private String customerId;
    private CheckoutDetailsRequest.BillingInfo billing;
    private CheckoutDetailsRequest.ShippingInfo shipping;
    private boolean shippingSameAsBilling;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CheckoutDetailsResponse() {
    }

    public CheckoutDetailsResponse(String id,
                                   String customerId,
                                   CheckoutDetailsRequest.BillingInfo billing,
                                   CheckoutDetailsRequest.ShippingInfo shipping,
                                   boolean shippingSameAsBilling,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.billing = billing;
        this.shipping = shipping;
        this.shippingSameAsBilling = shippingSameAsBilling;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public CheckoutDetailsRequest.BillingInfo getBilling() {
        return billing;
    }

    public void setBilling(CheckoutDetailsRequest.BillingInfo billing) {
        this.billing = billing;
    }

    public CheckoutDetailsRequest.ShippingInfo getShipping() {
        return shipping;
    }

    public void setShipping(CheckoutDetailsRequest.ShippingInfo shipping) {
        this.shipping = shipping;
    }

    public boolean isShippingSameAsBilling() {
        return shippingSameAsBilling;
    }

    public void setShippingSameAsBilling(boolean shippingSameAsBilling) {
        this.shippingSameAsBilling = shippingSameAsBilling;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
