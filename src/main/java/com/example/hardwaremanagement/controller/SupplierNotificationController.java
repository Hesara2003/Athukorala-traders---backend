package com.example.hardwaremanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notifications/supplier")
@CrossOrigin(origins = "http://localhost:5173")
public class SupplierNotificationController {

    @PostMapping("/po-created")
    public ResponseEntity<?> poCreated(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/po-approved")
    public ResponseEntity<?> poApproved(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/po-cancelled")
    public ResponseEntity<?> poCancelled(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/po-modified")
    public ResponseEntity<?> poModified(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/payment-received")
    public ResponseEntity<?> paymentReceived(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/delivery-reminder")
    public ResponseEntity<?> deliveryReminder(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/quality-issue")
    public ResponseEntity<?> qualityIssue(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @PostMapping("/custom-email")
    public ResponseEntity<?> customEmail(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("notification sent");
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<Map<String, Object>>> history(@PathVariable String id) {
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}/preferences")
    public ResponseEntity<?> getPreferences(@PathVariable String id) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("emailNotifications", true);
        prefs.put("smsNotifications", false);
        return ResponseEntity.ok(prefs);
    }

    @PatchMapping("/{id}/preferences")
    public ResponseEntity<?> updatePreferences(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(body);
    }

    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("test email sent");
    }
}
