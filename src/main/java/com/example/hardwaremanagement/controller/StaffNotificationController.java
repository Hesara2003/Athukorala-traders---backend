package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.service.StaffNotificationService;
import com.example.hardwaremanagement.model.StaffNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class StaffNotificationController {

    @Autowired
    private StaffNotificationService notificationService;

    /**
     * Server-Sent Events endpoint for real-time notifications
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications() {
        return notificationService.addEmitter();
    }

    /**
     * Get all notifications with pagination
     */
    @GetMapping
    public ResponseEntity<List<StaffNotification>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<StaffNotification> notifications = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get notification statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNotifications", notificationService.getTotalCount());
        stats.put("unreadCount", notificationService.getUnreadCount());
        stats.put("highPriorityCount", notificationService.getHighPriorityCount());
        stats.put("systemStatus", "OPERATIONAL");
        stats.put("lastUpdate", System.currentTimeMillis());
        return ResponseEntity.ok(stats);
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear all notifications
     */
    @DeleteMapping("/clear-all")
    public ResponseEntity<Void> clearAllNotifications() {
        notificationService.clearAllNotifications();
        return ResponseEntity.ok().build();
    }

    /**
     * Send a test notification (for demo purposes)
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> sendTestNotification(
            @RequestParam(defaultValue = "MEDIUM") String priority,
            @RequestParam(defaultValue = "Test notification") String message) {
        
        StaffNotification notification = notificationService.createTestNotification(priority, message);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("notificationId", notification.getId());
        response.put("message", "Test notification sent successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get notifications by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<StaffNotification>> getNotificationsByPriority(
            @PathVariable String priority) {
        List<StaffNotification> notifications = notificationService.getNotificationsByPriority(priority);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "StaffNotificationService");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
}
