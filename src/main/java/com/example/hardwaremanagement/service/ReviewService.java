package com.example.hardwaremanagement.service;

import com.example.hardwaremanagement.model.Order;
import com.example.hardwaremanagement.model.Review;
import com.example.hardwaremanagement.repository.OrderRepository;
import com.example.hardwaremanagement.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Create a new review
     */
    public Review createReview(Review review) {
        // Validate rating
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Check if customer has purchased this product
        if (review.getOrderId() != null && !review.getOrderId().isEmpty()) {
            Order order = orderRepository.findById(review.getOrderId()).orElse(null);
            if (order != null && order.getCustomerId().equals(review.getCustomerId())) {
                boolean hasPurchased = order.getItems().stream()
                    .anyMatch(item -> item.getProductId().equals(review.getProductId()));
                review.setVerifiedPurchase(hasPurchased);
            }
        }

        // Check for duplicate review
        var existing = reviewRepository.findByOrderIdAndProductIdAndCustomerId(
            review.getOrderId(), review.getProductId(), review.getCustomerId());
        
        if (existing.isPresent()) {
            throw new RuntimeException("You have already reviewed this product");
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setStatus("APPROVED"); // Auto-approve for now, can add moderation later
        review.setHelpfulCount(0);
        review.setNotHelpfulCount(0);

        return reviewRepository.save(review);
    }

    /**
     * Get all approved reviews for a product
     */
    public List<Review> getProductReviews(String productId) {
        return reviewRepository.findByProductIdAndStatus(productId, "APPROVED");
    }

    /**
     * Get reviews by customer
     */
    public List<Review> getCustomerReviews(String customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    /**
     * Update review
     */
    public Review updateReview(String reviewId, Review update) {
        Review existing = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        // Only allow customer to update their own review
        if (!existing.getCustomerId().equals(update.getCustomerId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (update.getRating() >= 1 && update.getRating() <= 5) {
            existing.setRating(update.getRating());
        }
        if (update.getTitle() != null) {
            existing.setTitle(update.getTitle());
        }
        if (update.getComment() != null) {
            existing.setComment(update.getComment());
        }
        if (update.getImages() != null) {
            existing.setImages(update.getImages());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        existing.setStatus("APPROVED"); // Re-approve after update

        return reviewRepository.save(existing);
    }

    /**
     * Delete review
     */
    public void deleteReview(String reviewId, String customerId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getCustomerId().equals(customerId)) {
            throw new RuntimeException("Unauthorized");
        }

        reviewRepository.deleteById(reviewId);
    }

    /**
     * Mark review as helpful
     */
    public Review markHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        return reviewRepository.save(review);
    }

    /**
     * Mark review as not helpful
     */
    public Review markNotHelpful(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setNotHelpfulCount(review.getNotHelpfulCount() + 1);
        return reviewRepository.save(review);
    }

    /**
     * Admin: Respond to review
     */
    public Review respondToReview(String reviewId, String response) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setAdminResponse(response);
        review.setAdminRespondedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    /**
     * Get product rating statistics
     */
    public Map<String, Object> getProductRatingStats(String productId) {
        List<Review> reviews = reviewRepository.findByProductIdAndStatus(productId, "APPROVED");

        if (reviews.isEmpty()) {
            return Map.of(
                "averageRating", 0.0,
                "totalReviews", 0,
                "ratingDistribution", Map.of(1, 0, 2, 0, 3, 0, 4, 0, 5, 0)
            );
        }

        double average = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        Map<Integer, Long> distribution = reviews.stream()
            .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", Math.round(average * 10.0) / 10.0);
        stats.put("totalReviews", reviews.size());
        stats.put("ratingDistribution", distribution);
        stats.put("fiveStarCount", distribution.getOrDefault(5, 0L));
        stats.put("fourStarCount", distribution.getOrDefault(4, 0L));
        stats.put("threeStarCount", distribution.getOrDefault(3, 0L));
        stats.put("twoStarCount", distribution.getOrDefault(2, 0L));
        stats.put("oneStarCount", distribution.getOrDefault(1, 0L));

        return stats;
    }

    /**
     * Get pending reviews for moderation
     */
    public List<Review> getPendingReviews() {
        return reviewRepository.findByStatus("PENDING");
    }

    /**
     * Approve review
     */
    public Review approveReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setStatus("APPROVED");
        return reviewRepository.save(review);
    }

    /**
     * Reject review
     */
    public Review rejectReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setStatus("REJECTED");
        return reviewRepository.save(review);
    }
}
