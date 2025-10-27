package com.example.hardwaremanagement.controller;

import com.example.hardwaremanagement.model.Review;
import com.example.hardwaremanagement.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        try {
            Review created = reviewService.createReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<?> getProductRatingStats(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductRatingStats(productId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerReviews(@PathVariable String customerId) {
        return ResponseEntity.ok(reviewService.getCustomerReviews(customerId));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable String reviewId, @RequestBody Review review) {
        try {
            Review updated = reviewService.updateReview(reviewId, review);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId, @RequestParam String customerId) {
        try {
            reviewService.deleteReview(reviewId, customerId);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<?> markHelpful(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.markHelpful(reviewId));
    }

    @PostMapping("/{reviewId}/not-helpful")
    public ResponseEntity<?> markNotHelpful(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.markNotHelpful(reviewId));
    }

    @PostMapping("/{reviewId}/respond")
    public ResponseEntity<?> respondToReview(@PathVariable String reviewId, @RequestBody Map<String, String> body) {
        String response = body.get("response");
        if (response == null || response.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Response is required"));
        }
        return ResponseEntity.ok(reviewService.respondToReview(reviewId, response));
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @PostMapping("/{reviewId}/approve")
    public ResponseEntity<?> approveReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId));
    }

    @PostMapping("/{reviewId}/reject")
    public ResponseEntity<?> rejectReview(@PathVariable String reviewId) {
        return ResponseEntity.ok(reviewService.rejectReview(reviewId));
    }
}
