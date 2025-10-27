package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Coupon;
import com.example.hardwaremanagement.model.CouponUsage;
import com.example.hardwaremanagement.model.OrderItem;
import com.example.hardwaremanagement.repository.CouponRepository;
import com.example.hardwaremanagement.repository.CouponUsageRepository;
import com.example.hardwaremanagement.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository usageRepository;
    private final OrderRepository orderRepository;

    public CouponService(CouponRepository couponRepository, 
                        CouponUsageRepository usageRepository,
                        OrderRepository orderRepository) {
        this.couponRepository = couponRepository;
        this.usageRepository = usageRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Validate and calculate discount for a coupon
     */
    public Map<String, Object> validateCoupon(String code, String customerId, double orderTotal, List<OrderItem> items) {
        Coupon coupon = couponRepository.findByCodeAndIsActive(code, true)
            .orElseThrow(() -> new RuntimeException("Invalid or inactive coupon code"));

        Map<String, Object> result = new HashMap<>();
        result.put("valid", false);
        result.put("discountAmount", 0.0);
        result.put("message", "");

        // Check validity period
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
            result.put("message", "Coupon is not yet valid");
            return result;
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            result.put("message", "Coupon has expired");
            return result;
        }

        // Check usage limit
        if (coupon.getUsageLimit() > 0 && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            result.put("message", "Coupon usage limit reached");
            return result;
        }

        // Check per-customer limit
        if (coupon.getPerCustomerLimit() > 0) {
            long customerUsage = usageRepository.countByCouponIdAndCustomerId(coupon.getId(), customerId);
            if (customerUsage >= coupon.getPerCustomerLimit()) {
                result.put("message", "You have already used this coupon maximum times");
                return result;
            }
        }

        // Check first-time customer restriction
        if (coupon.isFirstTimeCustomerOnly()) {
            long previousOrders = orderRepository.count(); // Simplified check
            if (previousOrders > 0) {
                result.put("message", "This coupon is only for first-time customers");
                return result;
            }
        }

        // Check minimum order amount
        if (coupon.getMinOrderAmount() > 0 && orderTotal < coupon.getMinOrderAmount()) {
            result.put("message", String.format("Minimum order amount is Rs. %.2f", coupon.getMinOrderAmount()));
            return result;
        }

        // Calculate eligible amount (for category/product restrictions)
        double eligibleAmount = calculateEligibleAmount(coupon, items);
        
        if (eligibleAmount == 0) {
            result.put("message", "No eligible items in cart for this coupon");
            return result;
        }

        // Calculate discount
        double discount = 0.0;
        if ("PERCENTAGE".equals(coupon.getType())) {
            discount = eligibleAmount * (coupon.getValue() / 100.0);
        } else if ("FIXED_AMOUNT".equals(coupon.getType())) {
            discount = coupon.getValue();
        }

        // Apply maximum discount cap
        if (coupon.getMaxDiscountAmount() > 0 && discount > coupon.getMaxDiscountAmount()) {
            discount = coupon.getMaxDiscountAmount();
        }

        // Don't allow discount to exceed eligible amount
        if (discount > eligibleAmount) {
            discount = eligibleAmount;
        }

        result.put("valid", true);
        result.put("discountAmount", Math.round(discount * 100.0) / 100.0);
        result.put("message", "Coupon applied successfully!");
        result.put("couponId", coupon.getId());
        result.put("couponCode", coupon.getCode());
        result.put("description", coupon.getDescription());

        return result;
    }

    /**
     * Calculate eligible amount based on category/product restrictions
     */
    private double calculateEligibleAmount(Coupon coupon, List<OrderItem> items) {
        double eligible = 0.0;

        for (OrderItem item : items) {
            boolean isEligible = true;

            // Check excluded products
            if (coupon.getExcludedProducts() != null && 
                coupon.getExcludedProducts().contains(item.getProductId())) {
                isEligible = false;
            }

            // Check applicable products (if specified)
            if (isEligible && coupon.getApplicableProducts() != null && 
                !coupon.getApplicableProducts().isEmpty()) {
                isEligible = coupon.getApplicableProducts().contains(item.getProductId());
            }

            // Check applicable categories (if specified)
            if (isEligible && coupon.getApplicableCategories() != null && 
                !coupon.getApplicableCategories().isEmpty()) {
                // This would require category information in OrderItem
                // For now, assume eligible if no category restriction
            }

            if (isEligible) {
                eligible += item.getUnitPrice() * item.getQuantity();
            }
        }

        return eligible;
    }

    /**
     * Record coupon usage
     */
    public void recordUsage(String couponId, String customerId, String orderId, double discountAmount) {
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Increment usage count
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        couponRepository.save(coupon);

        // Record usage
        CouponUsage usage = new CouponUsage();
        usage.setCouponId(couponId);
        usage.setCouponCode(coupon.getCode());
        usage.setCustomerId(customerId);
        usage.setOrderId(orderId);
        usage.setDiscountAmount(discountAmount);
        usage.setUsedAt(LocalDateTime.now());
        usageRepository.save(usage);
    }

    /**
     * Create a new coupon
     */
    public Coupon createCoupon(Coupon coupon) {
        // Check if code already exists
        if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }

        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUsageCount(0);
        return couponRepository.save(coupon);
    }

    /**
     * Update coupon
     */
    public Coupon updateCoupon(String id, Coupon update) {
        Coupon existing = couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));

        // Update fields
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        if (update.getValidFrom() != null) existing.setValidFrom(update.getValidFrom());
        if (update.getValidUntil() != null) existing.setValidUntil(update.getValidUntil());
        if (update.getUsageLimit() > 0) existing.setUsageLimit(update.getUsageLimit());
        if (update.getMinOrderAmount() >= 0) existing.setMinOrderAmount(update.getMinOrderAmount());
        existing.setActive(update.isActive());

        return couponRepository.save(existing);
    }

    /**
     * Delete coupon
     */
    public void deleteCoupon(String id) {
        couponRepository.deleteById(id);
    }

    /**
     * Get all coupons
     */
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    /**
     * Get active coupons
     */
    public List<Coupon> getActiveCoupons() {
        return couponRepository.findByIsActive(true);
    }

    /**
     * Get coupon usage history
     */
    public List<CouponUsage> getCouponUsageHistory(String couponId) {
        return usageRepository.findByCouponId(couponId);
    }

    /**
     * Get customer's coupon usage
     */
    public List<CouponUsage> getCustomerCouponHistory(String customerId) {
        return usageRepository.findByCustomerId(customerId);
    }
}
