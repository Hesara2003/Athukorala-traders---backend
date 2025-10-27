package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.model.OrderStatus;
import com.example.hardwaremanagement.repository.OrderRepository;
import com.example.hardwaremanagement.service.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Delivery Staff operations
 * Provides endpoints for delivery staff to view and manage their assigned orders
 *
 * Security:
 * - All endpoints require DELIVERY_STAFF role
 * - Proper JWT token required in Authorization header
 */
@RestController
@RequestMapping("/api/delivery-staff")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5177", "http://localhost:3000"})
public class DeliveryStaffController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired(required = false)
    private EmailNotificationService emailNotificationService;

    /**
     * GET /api/delivery-staff/my-orders
     * Fetch orders assigned to the logged-in delivery staff with status READY_TO_DISPATCH
     *
     * @return List of orders assigned to the delivery staff
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('DELIVERY_STAFF')")
    public ResponseEntity<?> getMyOrders() {
        try {
            // Get authenticated user info
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String deliveryStaffId = authentication.getName(); // Username is the user ID

            // Fetch orders assigned to this delivery staff with READY_TO_DISPATCH status
            List<Order> assignedOrders = orderRepository.findByDeliveryStaffIdAndStatusOrderByPlacedAtDesc(
                    deliveryStaffId,
                    OrderStatus.READY_TO_DISPATCH
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", assignedOrders.size());
            response.put("orders", assignedOrders);
            response.put("message", "Fetched " + assignedOrders.size() + " orders ready for dispatch");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch assigned orders");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/delivery-staff/orders/{orderId}
     * Get a specific order by ID (only if assigned to the logged-in delivery staff)
     *
     * @param orderId The order ID
     * @return The order details
     */
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('DELIVERY_STAFF')")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String deliveryStaffId = authentication.getName();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            // Verify this order is assigned to the current delivery staff
            if (order.getDeliveryStaffId() == null || !order.getDeliveryStaffId().equals(deliveryStaffId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Unauthorized access");
                error.put("message", "This order is not assigned to you");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", order);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Order not found or error occurred");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch order details");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/delivery-staff/stats
     * Get statistics for the logged-in delivery staff
     *
     * @return Statistics about assigned orders
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('DELIVERY_STAFF')")
    public ResponseEntity<?> getStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String deliveryStaffId = authentication.getName();

            // Count orders by different statuses
            List<Order> readyOrders = orderRepository.findByDeliveryStaffIdAndStatus(
                    deliveryStaffId, OrderStatus.READY_TO_DISPATCH);
            List<Order> outForDelivery = orderRepository.findByDeliveryStaffIdAndStatus(
                    deliveryStaffId, OrderStatus.OUT_FOR_DELIVERY);
            List<Order> delivered = orderRepository.findByDeliveryStaffIdAndStatus(
                    deliveryStaffId, OrderStatus.DELIVERED);

            Map<String, Object> stats = new HashMap<>();
            stats.put("readyToDispatch", readyOrders.size());
            stats.put("outForDelivery", outForDelivery.size());
            stats.put("delivered", delivered.size());
            stats.put("totalAssigned", readyOrders.size() + outForDelivery.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch statistics");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PATCH /api/delivery-staff/orders/{orderId}/status
     * Update the delivery status of an order
     *
     * Allowed status transitions:
     * - READY_TO_DISPATCH -> OUT_FOR_DELIVERY
     * - OUT_FOR_DELIVERY -> DELIVERED
     * - OUT_FOR_DELIVERY -> DELIVERY_ATTEMPTED
     *
     * @param orderId The order ID
     * @param requestBody Map containing the new status
     * @return Updated order details
     */
    @PatchMapping("/orders/{orderId}/status")
    @PreAuthorize("hasRole('DELIVERY_STAFF')")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> requestBody) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String deliveryStaffId = authentication.getName();

            // Get the order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            // Verify this order is assigned to the current delivery staff
            if (order.getDeliveryStaffId() == null || !order.getDeliveryStaffId().equals(deliveryStaffId)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Unauthorized access");
                error.put("message", "This order is not assigned to you");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            // Get the new status from request
            String newStatusStr = requestBody.get("status");
            if (newStatusStr == null || newStatusStr.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Status is required");
                error.put("message", "Please provide a status field in the request body");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Parse the new status
            OrderStatus newStatus;
            try {
                newStatus = OrderStatus.valueOf(newStatusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Invalid status");
                error.put("message", "Valid statuses: OUT_FOR_DELIVERY, DELIVERED, DELIVERY_ATTEMPTED");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validate status transitions
            OrderStatus currentStatus = order.getStatus();
            boolean validTransition = false;

            if (currentStatus == OrderStatus.READY_TO_DISPATCH && newStatus == OrderStatus.OUT_FOR_DELIVERY) {
                validTransition = true;
            } else if (currentStatus == OrderStatus.OUT_FOR_DELIVERY &&
                    (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.DELIVERY_ATTEMPTED)) {
                validTransition = true;
            } else if (currentStatus == OrderStatus.DELIVERY_ATTEMPTED &&
                    (newStatus == OrderStatus.OUT_FOR_DELIVERY || newStatus == OrderStatus.DELIVERED)) {
                validTransition = true;
            }

            if (!validTransition) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Invalid status transition");
                error.put("message", "Cannot change status from " + currentStatus + " to " + newStatus);
                error.put("currentStatus", currentStatus.toString());
                error.put("allowedTransitions", getAllowedTransitions(currentStatus));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Update the status
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);

            // Send email notification if order is marked as DELIVERED
            if (newStatus == OrderStatus.DELIVERED) {
                sendDeliveryNotification(order);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order status updated to " + newStatus);
            response.put("order", updatedOrder);
            response.put("previousStatus", currentStatus.toString());
            response.put("newStatus", newStatus.toString());
            response.put("notificationSent", newStatus == OrderStatus.DELIVERED);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Order not found or error occurred");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to update order status");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Helper method to get allowed status transitions for a given status
     */
    private List<String> getAllowedTransitions(OrderStatus currentStatus) {
        List<String> transitions = new java.util.ArrayList<>();

        switch (currentStatus) {
            case READY_TO_DISPATCH:
                transitions.add("OUT_FOR_DELIVERY");
                break;
            case OUT_FOR_DELIVERY:
                transitions.add("DELIVERED");
                transitions.add("DELIVERY_ATTEMPTED");
                break;
            case DELIVERY_ATTEMPTED:
                transitions.add("OUT_FOR_DELIVERY");
                transitions.add("DELIVERED");
                break;
            default:
                break;
        }

        return transitions;
    }

    /**
     * Send email notification to customer when order is delivered
     */
    private void sendDeliveryNotification(Order order) {
        try {
            // Get customer email
            String customerEmail = getCustomerEmail(order);
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                System.out.println("Warning: No email address found for order " + order.getId());
                return;
            }

            // Skip if email service is not configured
            if (emailNotificationService == null) {
                System.out.println("Info: Email service not configured. Notification skipped for order " + order.getId());
                return;
            }

            // Create email subject and body
            String subject = "Order Delivered - Order #" + order.getId();
            String htmlBody = buildDeliveryEmailHtml(order);

            // Send email
            emailNotificationService.send(customerEmail, subject, htmlBody);
            System.out.println("Delivery notification sent to " + customerEmail + " for order " + order.getId());

        } catch (Exception e) {
            // Log error but don't fail the status update
            System.err.println("Failed to send delivery notification for order " + order.getId() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get customer email from order
     */
    private String getCustomerEmail(Order order) {
        // Try order's customerEmail field first
        if (order.getCustomerEmail() != null && !order.getCustomerEmail().trim().isEmpty()) {
            return order.getCustomerEmail();
        }

        // Try billing email
        if (order.getBilling() != null && order.getBilling().getEmail() != null) {
            return order.getBilling().getEmail();
        }

        return null;
    }

    /**
     * Build HTML email body for delivery notification
     */
    private String buildDeliveryEmailHtml(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String deliveryDate = java.time.LocalDateTime.now().format(formatter);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { background-color: #f9f9f9; padding: 20px; }");
        html.append(".order-info { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #4CAF50; }");
        html.append(".footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }");
        html.append("</style></head><body>");
        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>&#10004; Order Delivered Successfully!</h1>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");
        html.append("<p>Dear Customer,</p>");
        html.append("<p>Great news! Your order has been successfully delivered.</p>");

        // Order info
        html.append("<div class='order-info'>");
        html.append("<strong>Order Details:</strong><br>");
        html.append("<strong>Order ID:</strong> " + order.getId() + "<br>");
        html.append("<strong>Delivery Date:</strong> " + deliveryDate + "<br>");
        html.append("<strong>Total Amount:</strong> Rs. " + String.format("%.2f", order.getTotalAmount()) + "<br>");

        if (order.getShipping() != null) {
            html.append("<strong>Delivery Address:</strong><br>");
            if (order.getShipping().getAddress() != null) {
                html.append(order.getShipping().getAddress() + "<br>");
            }
            if (order.getShipping().getCity() != null && order.getShipping().getPostal() != null) {
                html.append(order.getShipping().getCity() + ", " + order.getShipping().getPostal() + "<br>");
            }
        }
        html.append("</div>");

        // Items summary
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            html.append("<div class='order-info'>");
            html.append("<strong>Items Delivered:</strong><br>");
            html.append("<ul>");
            for (OrderItem item : order.getItems()) {
                html.append("<li>" + item.getName() + " (Qty: " + item.getQuantity() + ")</li>");
            }
            html.append("</ul>");
            html.append("</div>");
        }

        html.append("<p>Thank you for shopping with Athukorala Traders!</p>");
        html.append("<p>If you have any questions or concerns about your order, please don't hesitate to contact us.</p>");
        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p>This is an automated notification from Athukorala Traders.<br>");
        html.append("Please do not reply to this email.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}