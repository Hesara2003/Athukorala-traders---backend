package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Coupon;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "http://localhost:5173")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCoupon(@RequestBody ValidateRequest request) {
        try {
            Map<String, Object> result = couponService.validateCoupon(
                request.code,
                request.customerId,
                request.orderTotal,
                request.items
            );
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        try {
            Coupon created = couponService.createCoupon(coupon);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable String id, @RequestBody Coupon coupon) {
        try {
            Coupon updated = couponService.updateCoupon(id, coupon);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok(Map.of("message", "Coupon deleted successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }

    @GetMapping("/{id}/usage")
    public ResponseEntity<?> getCouponUsage(@PathVariable String id) {
        return ResponseEntity.ok(couponService.getCouponUsageHistory(id));
    }

    @GetMapping("/customer/{customerId}/history")
    public ResponseEntity<?> getCustomerCouponHistory(@PathVariable String customerId) {
        return ResponseEntity.ok(couponService.getCustomerCouponHistory(customerId));
    }

    public static class ValidateRequest {
        public String code;
        public String customerId;
        public double orderTotal;
        public List<OrderItem> items;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        
        public double getOrderTotal() { return orderTotal; }
        public void setOrderTotal(double orderTotal) { this.orderTotal = orderTotal; }
        
        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }
    }
}
