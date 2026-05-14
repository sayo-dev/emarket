package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.CreateReviewRequest;
import org.example.e_market.dto.requests.UpdateReviewRequest;
import org.example.e_market.dto.responses.ProductReviewsResponse;
import org.example.e_market.dto.responses.ReviewResponse;
import org.example.e_market.services.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/order-item/{orderItemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long orderItemId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review created successfully", reviewService.createReview(orderItemId, request)));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", reviewService.updateReview(reviewId, request)));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully", null));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ProductReviewsResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched successfully", reviewService.getProductReviews(productId)));
    }
}
