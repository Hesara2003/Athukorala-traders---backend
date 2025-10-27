package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends MongoRepository<Coupon, String> {
    Optional<Coupon> findByCodeAndIsActive(String code, boolean isActive);
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByIsActive(boolean isActive);
    List<Coupon> findByValidUntilAfter(LocalDateTime date);
}
