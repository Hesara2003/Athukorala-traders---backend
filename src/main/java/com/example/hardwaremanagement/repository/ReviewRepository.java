package com.example.hardwaremanagement.repository;

import com.example.hardwaremanagement.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByProductIdAndStatus(String productId, String status);
    List<Review> findByProductId(String productId);
    List<Review> findByCustomerId(String customerId);
    List<Review> findByStatus(String status);
    Optional<Review> findByOrderIdAndProductIdAndCustomerId(String orderId, String productId, String customerId);
    long countByProductIdAndStatus(String productId, String status);
}
