package com.example.hardwaremanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierInvoiceMatchingController {

    @GetMapping("/{id}/invoices/unmatched")
    public ResponseEntity<List<Map<String, Object>>> unmatchedInvoices(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}/purchase-orders/unmatched")
    public ResponseEntity<List<Map<String, Object>>> unmatchedPOs(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/invoices/{invoiceId}/auto-match")
    public ResponseEntity<?> autoMatch(@PathVariable String invoiceId) {
        Map<String, Object> res = new HashMap<>();
        res.put("matched", true);
        res.put("poId", "PO-123");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/invoices/{invoiceId}/match")
    public ResponseEntity<?> manualMatch(@PathVariable String invoiceId, @RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        res.put("matched", true);
        res.put("poId", body.get("poId"));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/invoices/{invoiceId}/match-suggestions")
    public ResponseEntity<List<Map<String, Object>>> matchSuggestions(@PathVariable String invoiceId) {
        List<Map<String, Object>> suggestions = new ArrayList<>();
        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("poId", "PO-123");
        suggestion.put("score", 95);
        suggestions.add(suggestion);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/invoices/{invoiceId}/compare/{poId}")
    public ResponseEntity<?> compare(@PathVariable String invoiceId, @PathVariable String poId) {
        Map<String, Object> comp = new HashMap<>();
        comp.put("invoiceTotal", 1000.0);
        comp.put("poTotal", 1000.0);
        comp.put("difference", 0.0);
        return ResponseEntity.ok(comp);
    }

    @PostMapping("/invoices/{invoiceId}/unmatch")
    public ResponseEntity<?> unmatch(@PathVariable String invoiceId) {
        return ResponseEntity.ok("unmatched");
    }

    @GetMapping("/{id}/invoices/matched")
    public ResponseEntity<List<Map<String, Object>>> matchedInvoices(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}/matching/stats")
    public ResponseEntity<?> matchingStats(@PathVariable String id) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInvoices", 100);
        stats.put("matched", 90);
        stats.put("unmatched", 10);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/invoices/{invoiceId}/validate")
    public ResponseEntity<?> validate(@PathVariable String invoiceId) {
        Map<String, Object> res = new HashMap<>();
        res.put("valid", true);
        res.put("errors", new ArrayList<>());
        return ResponseEntity.ok(res);
    }
}
