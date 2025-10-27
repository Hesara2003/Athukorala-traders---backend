package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.CouponUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CouponUsageRepository extends MongoRepository<CouponUsage, String> {
    List<CouponUsage> findByCouponId(String couponId);
    List<CouponUsage> findByCustomerId(String customerId);
    long countByCouponIdAndCustomerId(String couponId, String customerId);
}
