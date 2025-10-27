package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.dto.ProductDTO;
import com.example.hardwaremanagement.model.Category;
import com.example.hardwaremanagement.model.Product;
import com.example.hardwaremanagement.model.Promotion;
import com.example.hardwaremanagement.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CategoryService categoryService;

    // Get all products as DTOs
    public List<ProductDTO> getAllProductsDTO() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private ProductDTO toDto(Product p) {
        Promotion best = promotionService.getBestPromotionFor(p.getId(), p.getCategoryId()).orElse(null);
        double discountedPrice = p.getPrice();
        Double discountPercent = null; String promotionName = null;
        if (best != null) {
            discountPercent = best.getDiscountPercent();
            promotionName = best.getName();
            discountedPrice = p.getPrice() * (1 - discountPercent / 100.0);
        }
        String categoryName = p.getCategory();
        if (categoryName == null && p.getCategoryId() != null) {
            try { Category cat = categoryService.get(p.getCategoryId()); categoryName = cat.getName(); } catch (RuntimeException ignored) {}
        }
        return new ProductDTO(p.getId(), p.getName(), p.getPrice(), discountedPrice, discountPercent, promotionName, p.getImages(), categoryName, p.getStock());
    }
}
