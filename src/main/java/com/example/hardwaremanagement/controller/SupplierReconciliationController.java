package com.example.hardwaremanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierReconciliationController {

    @GetMapping("/{id}/reconciliation/dashboard")
    public ResponseEntity<?> dashboard(@PathVariable String id) {
        Map<String, Object> dash = new HashMap<>();
        dash.put("totalPOs", 50);
        dash.put("totalInvoices", 45);
        dash.put("matchedInvoices", 40);
        dash.put("unmatchedInvoices", 5);
        dash.put("discrepancies", 2);
        dash.put("outstandingBalance", 5000.0);
        return ResponseEntity.ok(dash);
    }

    @GetMapping("/{id}/reconciliation/report")
    public ResponseEntity<?> report(@PathVariable String id) {
        Map<String, Object> report = new HashMap<>();
        report.put("summary", "All matched");
        report.put("details", new ArrayList<>());
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reconciliation/po/{poId}/comparison")
    public ResponseEntity<?> poComparison(@PathVariable String poId) {
        Map<String, Object> comp = new HashMap<>();
        comp.put("poId", poId);
        comp.put("deliveredQty", 100);
        comp.put("orderedQty", 100);
        comp.put("discrepancy", 0);
        return ResponseEntity.ok(comp);
    }

    @GetMapping("/{id}/reconciliation/discrepancies")
    public ResponseEntity<List<Map<String, Object>>> discrepancies(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/reconciliation/discrepancies/{discrepancyId}/resolve")
    public ResponseEntity<?> resolveDiscrepancy(@PathVariable String discrepancyId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("resolved");
    }

    @GetMapping("/{id}/reconciliation/payments")
    public ResponseEntity<List<Map<String, Object>>> payments(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}/reconciliation/balance")
    public ResponseEntity<?> balance(@PathVariable String id) {
        Map<String, Object> bal = new HashMap<>();
        bal.put("outstandingBalance", 2500.0);
        bal.put("currency", "LKR");
        return ResponseEntity.ok(bal);
    }

    @GetMapping("/{id}/reconciliation/export")
    public ResponseEntity<?> export(@PathVariable String id) {
        return ResponseEntity.ok("csv export stub");
    }

    @PostMapping("/{id}/reconciliation/statement")
    public ResponseEntity<?> generateStatement(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> statement = new HashMap<>();
        statement.put("id", UUID.randomUUID().toString());
        statement.put("status", "generated");
        return ResponseEntity.ok(statement);
    }
}
