package com.example.hardwaremanagement.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "promotions")
public class Promotion {
    @Id
    private String id;
    private String name;
    private PromotionScope scope = PromotionScope.PRODUCT; // PRODUCT, CATEGORY, GLOBAL
    private String productId; // when scope == PRODUCT
    private String categoryId; // when scope == CATEGORY
    private double discountPercent; // 0 - 100
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active = true; // additional manual toggle

    // getters & setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PromotionScope getScope() { return scope; }
    public void setScope(PromotionScope scope) { this.scope = scope; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

