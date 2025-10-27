package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.OrderDetailDTO;
import com.example.hardwaremanagement.model.OrderStatus;
import com.example.hardwaremanagement.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fulfillment")
@CrossOrigin(origins = "http://localhost:5173")
public class FulfillmentController {

    private final OrderService orderService;

    public FulfillmentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<OrderDetailDTO> listPending() {
        return orderService.listPendingOrders();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        try {
            return ResponseEntity.ok(orderService.getById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PostMapping("/orders/{id}/pick/start")
    public ResponseEntity<?> startPick(@PathVariable String id) {
        try {
            OrderDetailDTO dto = orderService.updateStatus(id, OrderStatus.PICKED);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/orders/{id}/pick/items/{itemId}")
    public ResponseEntity<?> updatePickItem(@PathVariable String id, @PathVariable String itemId, @RequestBody(required = false) Object payload) {
        // For now this is a noop — the OrderService handles stock deduction when status becomes PICKED
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/orders/{id}/pick/complete")
    public ResponseEntity<?> completePick(@PathVariable String id) {
        try {
            OrderDetailDTO dto = orderService.updateStatus(id, OrderStatus.PACKED);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/orders/{id}/pack/start")
    public ResponseEntity<?> startPack(@PathVariable String id) {
        // Map to PACKED which will then be translated to READY_TO_DISPATCH in OrderService
        try {
            OrderDetailDTO dto = orderService.updateStatus(id, OrderStatus.PACKED);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/orders/{id}/delivery/schedule")
    public ResponseEntity<?> scheduleDelivery(@PathVariable String id, @RequestBody(required = false) Object body) {
        // Scheduling is not implemented in detail yet — return stub
        return ResponseEntity.ok("scheduled");
    }

    @PostMapping("/orders/{id}/delivery/assign")
    public ResponseEntity<?> assignDelivery(@PathVariable String id, @RequestBody(required = false) Object body) {
        return ResponseEntity.ok("assigned");
    }

    @PatchMapping("/orders/{id}/delivery/status")
    public ResponseEntity<?> updateDeliveryStatus(@PathVariable String id, @RequestBody MapPayload payload) {
        // payload.status expected
        return ResponseEntity.ok("ok");
    }

    public static class MapPayload {
        public String status;
        public String getStatus() { return status; }
        public void setStatus(String s) { this.status = s; }
    }
}
