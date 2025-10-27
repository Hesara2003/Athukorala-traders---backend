package com.example.hardwaremanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Document(collection = "staff_notifications")
public class StaffNotification {
    
    @Id
    private String id;
    
    private String purchaseOrderId;
    private String supplierId;
    private String title;
    private String message;
    private String priority; // HIGH, MEDIUM, LOW
    private String type; // DELIVERY_UPDATE, STOCK_ALERT, SYSTEM_MESSAGE, etc.
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime readAt;
    
    private boolean read;
    
    private String metadata; // Additional JSON data if needed
    
    // Constructors
    public StaffNotification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
    
    public StaffNotification(String title, String message, String priority, String type) {
        this();
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.type = type;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }
    
    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    // Utility methods
    public boolean isHighPriority() {
        return "HIGH".equalsIgnoreCase(this.priority);
    }
    
    public boolean isMediumPriority() {
        return "MEDIUM".equalsIgnoreCase(this.priority);
    }
    
    public boolean isLowPriority() {
        return "LOW".equalsIgnoreCase(this.priority);
    }
    
    @Override
    public String toString() {
        return "StaffNotification{" +
                "id='" + id + '\'' +
                ", purchaseOrderId='" + purchaseOrderId + '\'' +
                ", title='" + title + '\'' +
                ", priority='" + priority + '\'' +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", read=" + read +
                '}';
    }
}
