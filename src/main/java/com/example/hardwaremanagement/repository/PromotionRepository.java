package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Promotion;
import com.example.hardwaremanagement.model.PromotionScope;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    List<Promotion> findByScopeAndActiveTrueAndStartDateBeforeAndEndDateAfter(PromotionScope scope, LocalDateTime now1, LocalDateTime now2);
    List<Promotion> findByScopeAndProductIdAndActiveTrueAndStartDateBeforeAndEndDateAfter(PromotionScope scope, String productId, LocalDateTime now1, LocalDateTime now2);
    List<Promotion> findByScopeAndCategoryIdAndActiveTrueAndStartDateBeforeAndEndDateAfter(PromotionScope scope, String categoryId, LocalDateTime now1, LocalDateTime now2);
}
