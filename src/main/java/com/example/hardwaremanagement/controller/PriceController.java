package com.example.hardwaremanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/prices")
@CrossOrigin(origins = "http://localhost:5173")
public class PriceController {

    @PostMapping("/apply-promotions")
    public ResponseEntity<?> applyPromotions(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "applied");
        res.put("productsUpdated", 20);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/effective/{productId}")
    public ResponseEntity<?> effectivePrice(@PathVariable String productId) {
        Map<String, Object> price = new HashMap<>();
        price.put("productId", productId);
        price.put("basePrice", 100.0);
        price.put("effectivePrice", 90.0);
        price.put("discountPercent", 10.0);
        return ResponseEntity.ok(price);
    }

    @PostMapping("/effective/bulk")
    public ResponseEntity<List<Map<String, Object>>> bulkEffectivePrice(@RequestBody List<String> productIds) {
        List<Map<String, Object>> prices = new ArrayList<>();
        for (String pid : productIds) {
            Map<String, Object> price = new HashMap<>();
            price.put("productId", pid);
            price.put("basePrice", 100.0);
            price.put("effectivePrice", 100.0);
            prices.add(price);
        }
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/promotions/{productId}")
    public ResponseEntity<List<Map<String, Object>>> activePromotions(@PathVariable String productId) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/promotions/{id}/schedule")
    public ResponseEntity<?> schedulePromotion(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("scheduled");
    }

    @GetMapping("/history/{productId}")
    public ResponseEntity<List<Map<String, Object>>> priceHistory(@PathVariable String productId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}
