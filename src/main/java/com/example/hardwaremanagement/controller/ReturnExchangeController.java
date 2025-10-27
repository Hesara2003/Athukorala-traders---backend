package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.EligibleOrderDTO;
import com.example.hardwaremanagement.model.ReturnExchange;
import com.example.hardwaremanagement.model.ReturnExchangeStatus;
import com.example.hardwaremanagement.service.ReturnExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5177", "http://localhost:3000"})
public class ReturnExchangeController {

    @Autowired
    private ReturnExchangeService returnExchangeService;

    /**
     * GET /api/admin/returns/eligible-orders
     * Fetch all orders eligible for return/exchange (STAFF and ADMIN only)
     */
    @GetMapping("/admin/returns/eligible-orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getEligibleOrders() {
        try {
            List<EligibleOrderDTO> eligibleOrders = returnExchangeService.getEligibleOrdersForReturn();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", eligibleOrders.size());
            response.put("orders", eligibleOrders);
            response.put("message", "Fetched " + eligibleOrders.size() + " eligible orders");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch eligible orders");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/admin/returns/eligible-orders/{orderId}
     * Check eligibility of a specific order (STAFF and ADMIN only)
     */
    @GetMapping("/admin/returns/eligible-orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getOrderEligibility(@PathVariable String orderId) {
        try {
            EligibleOrderDTO eligibility = returnExchangeService.getOrderEligibility(orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", eligibility);
            
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
            error.put("error", "Failed to check order eligibility");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/customer/returns/my-eligible-orders
     * Fetch eligible orders for the logged-in customer
     */
    @GetMapping("/customer/returns/my-eligible-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getMyEligibleOrders() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String customerId = authentication.getName(); // Assuming username is customerId
            
            List<EligibleOrderDTO> eligibleOrders = returnExchangeService.getEligibleOrdersForCustomer(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", eligibleOrders.size());
            response.put("orders", eligibleOrders);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch your eligible orders");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * POST /api/returns
     * Create a new return/exchange request (Any authenticated user)
     */
    @PostMapping("/returns")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createReturnExchange(@RequestBody ReturnExchange returnExchange) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            
            // Set customer ID from authenticated user
            returnExchange.setCustomerId(userId);
            
            ReturnExchange created = returnExchangeService.createReturnExchange(returnExchange);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("returnExchange", created);
            response.put("message", "Return/Exchange request created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to create return/exchange request");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to create return/exchange request");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/admin/returns
     * Get all return/exchange requests (STAFF and ADMIN only)
     */
    @GetMapping("/admin/returns")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getAllReturnExchanges() {
        try {
            List<ReturnExchange> returns = returnExchangeService.getAllReturnExchanges();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", returns.size());
            response.put("returns", returns);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch return/exchange requests");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/admin/returns/pending
     * Get pending return/exchange requests for staff review (STAFF and ADMIN only)
     */
    @GetMapping("/admin/returns/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getPendingReturnExchanges() {
        try {
            List<ReturnExchange> pending = returnExchangeService.getPendingReturnExchanges();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", pending.size());
            response.put("returns", pending);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch pending returns");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/admin/returns/{id}
     * Get a specific return/exchange by ID (STAFF and ADMIN only)
     */
    @GetMapping("/admin/returns/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> getReturnExchangeById(@PathVariable String id) {
        try {
            ReturnExchange returnExchange = returnExchangeService.getReturnExchangeById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("returnExchange", returnExchange);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Return/Exchange not found");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to fetch return/exchange");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * PUT /api/admin/returns/{id}/process
     * Process a return/exchange request (update status) (STAFF and ADMIN only)
     */
    @PutMapping("/admin/returns/{id}/process")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<?> processReturnExchange(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String processedBy = authentication.getName();
            
            String statusStr = request.get("status");
            if (statusStr == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "Status is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            ReturnExchangeStatus newStatus = ReturnExchangeStatus.valueOf(statusStr);
            ReturnExchange processed = returnExchangeService.processReturnExchange(id, newStatus, processedBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("returnExchange", processed);
            response.put("message", "Return/Exchange processed successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Invalid status");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to process return/exchange");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to process return/exchange");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * DELETE /api/returns/{id}/cancel
     * Cancel a return/exchange request
     */
    @DeleteMapping("/returns/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelReturnExchange(@PathVariable String id) {
        try {
            ReturnExchange cancelled = returnExchangeService.cancelReturnExchange(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("returnExchange", cancelled);
            response.put("message", "Return/Exchange cancelled successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to cancel return/exchange");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Failed to cancel return/exchange");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
