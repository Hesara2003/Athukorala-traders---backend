package com.example.hardwaremanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "coupons")
public class Coupon {
    @Id
    private String id;
    private String code; // SUMMER2025, FIRST10, etc.
    private String description;
    private String type; // PERCENTAGE, FIXED_AMOUNT
    private double value; // 10 for 10%, or 500 for Rs.500 off
    private double minOrderAmount; // Minimum order value to apply
    private double maxDiscountAmount; // Maximum discount cap
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private int usageLimit; // Total number of times coupon can be used
    private int usageCount; // Current usage count
    private int perCustomerLimit; // Max uses per customer
    private boolean isActive;
    private List<String> applicableCategories; // Empty = all categories
    private List<String> applicableProducts; // Empty = all products
    private List<String> excludedProducts;
    private boolean firstTimeCustomerOnly;
    private LocalDateTime createdAt;
    private String createdBy;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public double getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(double maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public int getPerCustomerLimit() {
        return perCustomerLimit;
    }

    public void setPerCustomerLimit(int perCustomerLimit) {
        this.perCustomerLimit = perCustomerLimit;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<String> getApplicableCategories() {
        return applicableCategories;
    }

    public void setApplicableCategories(List<String> applicableCategories) {
        this.applicableCategories = applicableCategories;
    }

    public List<String> getApplicableProducts() {
        return applicableProducts;
    }

    public void setApplicableProducts(List<String> applicableProducts) {
        this.applicableProducts = applicableProducts;
    }

    public List<String> getExcludedProducts() {
        return excludedProducts;
    }

    public void setExcludedProducts(List<String> excludedProducts) {
        this.excludedProducts = excludedProducts;
    }

    public boolean isFirstTimeCustomerOnly() {
        return firstTimeCustomerOnly;
    }

    public void setFirstTimeCustomerOnly(boolean firstTimeCustomerOnly) {
        this.firstTimeCustomerOnly = firstTimeCustomerOnly;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
