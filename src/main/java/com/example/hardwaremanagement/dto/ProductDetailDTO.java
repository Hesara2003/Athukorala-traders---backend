package com.example.hardwaremanagement.dto;

import java.util.List;

public class ProductDetailDTO {
    private String id;
    private String name;
    private String category;
    private String description;
    private double price;
    private double discountedPrice;
    private Double discountPercent;
    private String promotionName;
    private int stock;
    private List<String> images;
    private String brand;
    private String model;
    private String specifications;
    private String warranty;
    private String weight;
    private String dimensions;
    private String color;
    private String material;
    private boolean isAvailable;
    private String sku;
    private String stockStatus; // "In Stock", "Low Stock", "Out of Stock"
    private List<ProductDTO> relatedProducts;

    // Default constructor
    public ProductDetailDTO() {}

    // Constructor
    public ProductDetailDTO(String id, String name, String category, String description, 
                           double price, double discountedPrice, Double discountPercent, String promotionName,
                           int stock, List<String> images, String brand, 
                           String model, String specifications, String warranty, String weight, 
                           String dimensions, String color, String material, boolean isAvailable, 
                           String sku, String stockStatus, List<ProductDTO> relatedProducts) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
        this.promotionName = promotionName;
        this.stock = stock;
        this.images = images;
        this.brand = brand;
        this.model = model;
        this.specifications = specifications;
        this.warranty = warranty;
        this.weight = weight;
        this.dimensions = dimensions;
        this.color = color;
        this.material = material;
        this.isAvailable = isAvailable;
        this.sku = sku;
        this.stockStatus = stockStatus;
        this.relatedProducts = relatedProducts;
    }

    // Getters and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public List<ProductDTO> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<ProductDTO> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }
}
