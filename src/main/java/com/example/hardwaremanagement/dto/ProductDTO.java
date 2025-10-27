package com.example.hardwaremanagement.dto;

import java.util.List;

public class ProductDTO {
    private String id; // Product ID for navigation
    private String name;
    private double price;
    private double discountedPrice;
    private Double discountPercent; // null if no promotion
    private String promotionName; // null if no promotion
    private List<String> images;
    private String category; // category name
    private int stock; // Stock quantity

    // Constructor (full)
    public ProductDTO(String id, String name, double price, double discountedPrice, Double discountPercent, String promotionName, List<String> images, String category, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
        this.promotionName = promotionName;
        this.images = images;
        this.category = category;
        this.stock = stock;
    }

    // Convenience constructor without promotion
    public ProductDTO(String id, String name, double price, List<String> images, String category, int stock) {
        this(id, name, price, price, null, null, images, category, stock);
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }

    public String getPromotionName() { return promotionName; }
    public void setPromotionName(String promotionName) { this.promotionName = promotionName; }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
