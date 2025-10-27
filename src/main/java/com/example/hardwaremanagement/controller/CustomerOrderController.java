package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.dto.CreateOrderRequest;
import com.example.hardwaremanagement.dto.OrderDetailDTO;
import com.example.hardwaremanagement.dto.UpdateOrderStatusRequest;
import com.example.hardwaremanagement.model.OrderStatus;
import com.example.hardwaremanagement.model.User;
import com.example.hardwaremanagement.repository.UserRepository;
import com.example.hardwaremanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDetailDTO>> listByCustomer(@RequestParam("customerId") String customerId) {
        return ResponseEntity.ok(orderService.listByCustomer(customerId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<OrderDetailDTO>> listPendingOrders() {
        return ResponseEntity.ok(orderService.listPendingOrders());
    }

    @GetMapping("/by-status")
    public ResponseEntity<List<OrderDetailDTO>> listByStatus(@RequestParam("status") String statusStr) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.listByStatus(status));
    }

    // Convenience endpoint for clients that only know the username
    @GetMapping("/by-username")
    public ResponseEntity<List<OrderDetailDTO>> listByUsername(@RequestParam("username") String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderService.listByCustomer(user.getId()));
    }

    @PostMapping
    public ResponseEntity<OrderDetailDTO> create(@RequestBody CreateOrderRequest req) {
        if (req == null || CollectionUtils.isEmpty(req.getItems())) {
            return ResponseEntity.badRequest().build();
        }
        if (!StringUtils.hasText(req.getCustomerId()) && !StringUtils.hasText(req.getCustomerEmail())) {
            return ResponseEntity.badRequest().build();
        }
        OrderDetailDTO saved = orderService.createOrder(req);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDetailDTO> updateStatus(@PathVariable String id,
                                                       @RequestBody UpdateOrderStatusRequest body) {
        if (body == null || body.getStatus() == null || body.getStatus().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(body.getStatus());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
