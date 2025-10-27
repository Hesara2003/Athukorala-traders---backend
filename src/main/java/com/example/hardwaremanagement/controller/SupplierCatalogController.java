package com.example.hardwaremanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierCatalogController {

    @PostMapping("/catalog/upload")
    public ResponseEntity<?> uploadCatalog(@RequestParam("file") MultipartFile file, @RequestParam(required = false) String supplierId) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("file is required");
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", UUID.randomUUID().toString());
        resp.put("filename", file.getOriginalFilename());
        resp.put("status", "uploaded");
        resp.put("rows", 42);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/catalog/history")
    public ResponseEntity<List<Map<String, Object>>> catalogHistory(@PathVariable String id) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", "import-1");
        item.put("uploadedAt", "2025-01-01T10:00:00");
        item.put("status", "completed");
        list.add(item);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/catalog/imports/{importId}")
    public ResponseEntity<?> getImport(@PathVariable String importId) {
        Map<String, Object> imp = new HashMap<>();
        imp.put("id", importId);
        imp.put("status", "completed");
        imp.put("rows", 42);
        return ResponseEntity.ok(imp);
    }

    @PostMapping("/{id}/catalog/sync")
    public ResponseEntity<?> syncCatalog(@PathVariable String id) {
        return ResponseEntity.ok("sync started");
    }

    @GetMapping("/{id}/catalog/sync/status")
    public ResponseEntity<?> syncStatus(@PathVariable String id) {
        Map<String, Object> st = new HashMap<>();
        st.put("status", "idle");
        return ResponseEntity.ok(st);
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<Map<String, Object>>> listProducts(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<?> createProduct(@PathVariable String id, @RequestBody Map<String, Object> body) {
        body.put("id", UUID.randomUUID().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/{id}/products/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @PathVariable String productId, @RequestBody Map<String, Object> body) {
        body.put("id", productId);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id, @PathVariable String productId) {
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/products/bulk-update-prices")
    public ResponseEntity<?> bulkUpdatePrices(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("updated", 10);
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{id}/products/bulk-update-stock")
    public ResponseEntity<?> bulkUpdateStock(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("updated", 10);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/catalog/template", produces = MediaType.TEXT_PLAIN_VALUE)
    public String downloadTemplate() {
        return "sku,name,price,stock\nEXAMPLE-SKU,Sample Product,100.00,50";
    }

    @GetMapping(value = "/{id}/catalog/export", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportCatalog(@PathVariable String id) {
        return "sku,name,price,stock\n";
    }

    @PostMapping("/catalog/validate")
    public ResponseEntity<?> validateCatalog(@RequestBody Map<String, Object> body) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("valid", true);
        resp.put("errors", new ArrayList<>());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/catalog/stats")
    public ResponseEntity<?> catalogStats(@PathVariable String id) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", 100);
        stats.put("activeProducts", 90);
        return ResponseEntity.ok(stats);
    }
}
