package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.StaffNotification;
import com.example.hardwaremanagement.repository.StaffNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.UUID;

@Service
public class StaffNotificationService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    @Autowired(required = false)
    private StaffNotificationRepository notificationRepository;

    /**
     * Add a new SSE emitter for real-time notifications
     */
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));
        
        emitters.add(emitter);
        
        // Send initial connection confirmation
        try {
            emitter.send(SseEmitter.event()
                .name("connection")
                .data("{\"status\":\"connected\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}"));
        } catch (Exception e) {
            emitters.remove(emitter);
        }
        
        return emitter;
    }

    /**
     * Send notification to all connected clients
     */
    private void sendSseNotification(StaffNotification notification) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                    .name("delivery-update")
                    .data(notification));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        
        emitters.removeAll(deadEmitters);
    }

    /**
     * Create and send a delivery timeline notification
     */
    public StaffNotification notifyDeliveryTimelineUpdate(String poId, String supplierId, 
            String originalDate, String newDate, String reason, String priority) {
        
        StaffNotification notification = new StaffNotification();
        notification.setId(UUID.randomUUID().toString());
        notification.setPurchaseOrderId(poId);
        notification.setSupplierId(supplierId);
        notification.setTitle(getNotificationTitle(priority));
        notification.setMessage(createDeliveryMessage(poId, originalDate, newDate, reason));
        notification.setPriority(priority);
        notification.setType("DELIVERY_UPDATE");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        // Save to database if repository is available
        if (notificationRepository != null) {
            notificationRepository.save(notification);
        }

        // Send real-time notification
        sendSseNotification(notification);
        
        return notification;
    }

    /**
     * Create a test notification
     */
    public StaffNotification createTestNotification(String priority, String message) {
        StaffNotification notification = new StaffNotification();
        notification.setId(UUID.randomUUID().toString());
        notification.setPurchaseOrderId("PO-TEST-" + System.currentTimeMillis());
        notification.setSupplierId("TEST-SUPPLIER");
        notification.setTitle("🧪 " + getNotificationTitle(priority) + " (Test)");
        notification.setMessage(message);
        notification.setPriority(priority.toUpperCase());
        notification.setType("TEST");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        // Save to database if repository is available
        if (notificationRepository != null) {
            notificationRepository.save(notification);
        }

        // Send real-time notification
        sendSseNotification(notification);
        
        return notification;
    }

    /**
     * Get all notifications with pagination
     */
    public List<StaffNotification> getAllNotifications(int page, int size) {
        if (notificationRepository != null) {
            return notificationRepository.findAllByOrderByCreatedAtDesc();
        }
        
        // Return sample data if no repository
        return getSampleNotifications();
    }

    /**
     * Get notifications by priority
     */
    public List<StaffNotification> getNotificationsByPriority(String priority) {
        if (notificationRepository != null) {
            return notificationRepository.findByPriorityOrderByCreatedAtDesc(priority.toUpperCase());
        }
        
        return getSampleNotifications().stream()
            .filter(n -> n.getPriority().equals(priority.toUpperCase()))
            .toList();
    }

    /**
     * Mark notification as read
     */
    public void markAsRead(String id) {
        if (notificationRepository != null) {
            StaffNotification notification = notificationRepository.findById(id).orElse(null);
            if (notification != null) {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
        }
    }

    /**
     * Mark all notifications as read
     */
    public void markAllAsRead() {
        if (notificationRepository != null) {
            List<StaffNotification> notifications = notificationRepository.findAll();
            notifications.forEach(notification -> {
                notification.setRead(true);
                notification.setReadAt(LocalDateTime.now());
            });
            notificationRepository.saveAll(notifications);
        }
    }

    /**
     * Delete a notification
     */
    public void deleteNotification(String id) {
        if (notificationRepository != null) {
            notificationRepository.deleteById(id);
        }
    }

    /**
     * Clear all notifications
     */
    public void clearAllNotifications() {
        if (notificationRepository != null) {
            notificationRepository.deleteAll();
        }
    }

    /**
     * Get total notification count
     */
    public long getTotalCount() {
        if (notificationRepository != null) {
            return notificationRepository.count();
        }
        return 0;
    }

    /**
     * Get unread notification count
     */
    public long getUnreadCount() {
        if (notificationRepository != null) {
            return notificationRepository.countByReadFalse();
        }
        return 0;
    }

    /**
     * Get high priority notification count
     */
    public long getHighPriorityCount() {
        if (notificationRepository != null) {
            return notificationRepository.countByPriorityAndReadFalse("HIGH");
        }
        return 0;
    }

    // Helper methods
    private String getNotificationTitle(String priority) {
        return switch (priority.toUpperCase()) {
            case "HIGH" -> "🚨 Urgent Delivery Update";
            case "MEDIUM" -> "📅 Delivery Schedule Change";
            case "LOW" -> "✅ Delivery Information";
            default -> "📦 Delivery Notification";
        };
    }

    private String createDeliveryMessage(String poId, String originalDate, String newDate, String reason) {
        return String.format(
            "Purchase Order %s: Delivery date updated from %s to %s. Reason: %s",
            poId, originalDate, newDate, reason
        );
    }

    private List<StaffNotification> getSampleNotifications() {
        // Return sample data for demo purposes
        return List.of(
            createSampleNotification("HIGH", "🚨 Critical Delay", "PO-2024-001: Hardware delivery delayed by 3 days due to supplier issues."),
            createSampleNotification("MEDIUM", "📅 Schedule Update", "PO-2024-002: Construction materials moved to next Tuesday."),
            createSampleNotification("LOW", "✅ Delivery Confirmed", "PO-2024-003: Garden tools confirmed for tomorrow morning.")
        );
    }

    private StaffNotification createSampleNotification(String priority, String title, String message) {
        StaffNotification notification = new StaffNotification();
        notification.setId(UUID.randomUUID().toString());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority);
        notification.setType("DELIVERY_UPDATE");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        return notification;
    }
}
