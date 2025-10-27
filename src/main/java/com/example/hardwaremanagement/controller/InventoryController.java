package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveStock(@RequestBody ReservationRequest request) {
        try {
            var reservation = inventoryService.reserveStock(
                request.orderId,
                request.productId,
                request.quantity,
                request.customerId,
                request.expiryMinutes != null ? request.expiryMinutes : 15 // Default 15 minutes
            );
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reserve/{orderId}/confirm")
    public ResponseEntity<?> confirmReservation(@PathVariable String orderId) {
        try {
            inventoryService.confirmReservation(orderId);
            return ResponseEntity.ok(Map.of("message", "Reservation confirmed", "orderId", orderId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reserve/{orderId}/release")
    public ResponseEntity<?> releaseReservation(@PathVariable String orderId, 
                                                @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : "CANCELLED";
        inventoryService.releaseReservation(orderId, reason);
        return ResponseEntity.ok(Map.of("message", "Reservation released", "orderId", orderId));
    }

    @GetMapping("/available/{productId}")
    public ResponseEntity<?> getAvailableStock(@PathVariable String productId) {
        try {
            int available = inventoryService.getAvailableStock(productId);
            return ResponseEntity.ok(Map.of("productId", productId, "availableStock", available));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{productId}")
    public ResponseEntity<?> getStockStatus(@PathVariable String productId) {
        try {
            return ResponseEntity.ok(inventoryService.getStockStatus(productId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkStock(@RequestBody Map<String, Integer> items) {
        Map<String, Boolean> results = inventoryService.checkStockBulk(items);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/alerts")
    public ResponseEntity<?> getLowStockAlerts() {
        inventoryService.checkLowStockAlerts();
        return ResponseEntity.ok(Map.of("message", "Low stock check completed"));
    }

    public static class ReservationRequest {
        public String orderId;
        public String productId;
        public int quantity;
        public String customerId;
        public Integer expiryMinutes;

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public Integer getExpiryMinutes() { return expiryMinutes; }
        public void setExpiryMinutes(Integer expiryMinutes) { this.expiryMinutes = expiryMinutes; }
    }
}
