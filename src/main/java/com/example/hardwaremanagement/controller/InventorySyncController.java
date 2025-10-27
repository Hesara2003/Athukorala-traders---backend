package com.example.hardwaremanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:5173")
public class InventorySyncController {

    @PostMapping("/sync/po-received")
    public ResponseEntity<?> syncPoReceived(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "synced");
        res.put("productsUpdated", 5);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/sync/order-fulfilled")
    public ResponseEntity<?> syncOrderFulfilled(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "synced");
        res.put("productsUpdated", 3);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/sync/return-processed")
    public ResponseEntity<?> syncReturnProcessed(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "synced");
        res.put("productsUpdated", 2);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/sync/adjust")
    public ResponseEntity<?> adjustInventory(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "adjusted");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/sync/log")
    public ResponseEntity<List<Map<String, Object>>> syncLog() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/sync/full")
    public ResponseEntity<?> fullSync() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "started");
        res.put("jobId", UUID.randomUUID().toString());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/sync/status")
    public ResponseEntity<?> syncStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "idle");
        status.put("lastSync", "2025-01-01T10:00:00");
        return ResponseEntity.ok(status);
    }

    @PostMapping("/sync/conflicts/{id}/resolve")
    public ResponseEntity<?> resolveConflict(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("resolved");
    }

    @PostMapping("/stock/bulk")
    public ResponseEntity<List<Map<String, Object>>> bulkStock(@RequestBody List<String> productIds) {
        List<Map<String, Object>> stocks = new ArrayList<>();
        for (String pid : productIds) {
            Map<String, Object> stock = new HashMap<>();
            stock.put("productId", pid);
            stock.put("stock", 100);
            stocks.add(stock);
        }
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<?> getStock(@PathVariable String productId) {
        Map<String, Object> stock = new HashMap<>();
        stock.put("productId", productId);
        stock.put("stock", 100);
        return ResponseEntity.ok(stock);
    }
}
