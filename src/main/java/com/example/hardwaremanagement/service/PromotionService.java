package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Promotion;
import com.example.hardwaremanagement.model.PromotionScope;
import com.example.hardwaremanagement.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    public Promotion create(Promotion promotion) { return promotionRepository.save(promotion); }
    public Promotion update(String id, Promotion updated) {
        Promotion existing = promotionRepository.findById(id).orElseThrow(() -> new RuntimeException("Promotion not found: " + id));
        existing.setName(updated.getName());
        existing.setScope(updated.getScope());
        existing.setProductId(updated.getProductId());
        existing.setCategoryId(updated.getCategoryId());
        existing.setDiscountPercent(updated.getDiscountPercent());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setActive(updated.isActive());
        return promotionRepository.save(existing);
    }
    public void delete(String id) { promotionRepository.deleteById(id); }
    public List<Promotion> list() { return promotionRepository.findAll(); }

        public Optional<Promotion> get(String id) { return promotionRepository.findById(id); }

    public Optional<Promotion> getBestPromotionFor(String productId, String categoryId) {
        LocalDateTime now = LocalDateTime.now();
        // Product specific
        List<Promotion> productPromos = promotionRepository.findByScopeAndProductIdAndActiveTrueAndStartDateBeforeAndEndDateAfter(
                PromotionScope.PRODUCT, productId, now, now);
        // Category
        List<Promotion> categoryPromos = categoryId == null ? List.of() : promotionRepository.findByScopeAndCategoryIdAndActiveTrueAndStartDateBeforeAndEndDateAfter(
                PromotionScope.CATEGORY, categoryId, now, now);
        // Global
        List<Promotion> globalPromos = promotionRepository.findByScopeAndActiveTrueAndStartDateBeforeAndEndDateAfter(
                PromotionScope.GLOBAL, now, now);

        Stream<Promotion> combined = Stream.concat(
                Stream.concat(productPromos.stream(), categoryPromos.stream()),
                globalPromos.stream()
        );
        return combined.max(Comparator.comparingDouble(Promotion::getDiscountPercent));
    }
}
