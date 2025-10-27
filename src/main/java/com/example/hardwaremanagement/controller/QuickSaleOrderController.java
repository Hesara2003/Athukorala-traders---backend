package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.QuickSaleOrder;
import com.example.hardwaremanagement.dto.QuickSaleRequestDTO;
import com.example.hardwaremanagement.service.QuickSaleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/quick-sales")
@CrossOrigin(origins = "*")
public class QuickSaleOrderController {

    @Autowired
    private QuickSaleOrderService quickSaleOrderService;

    @PostMapping
    public ResponseEntity<QuickSaleOrder> createOrder(@RequestBody QuickSaleRequestDTO request) {
        QuickSaleOrder order = quickSaleOrderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuickSaleOrder>> getAllOrders() {
        List<QuickSaleOrder> orders = quickSaleOrderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuickSaleOrder> getOrder(@PathVariable String id) {
        QuickSaleOrder order = quickSaleOrderService.getOrder(id);
        return ResponseEntity.ok(order);
    }
}