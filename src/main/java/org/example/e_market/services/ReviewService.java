package org.example.e_market.services;

import org.example.e_market.dto.requests.CreateReviewRequest;
import org.example.e_market.dto.requests.UpdateReviewRequest;
import org.example.e_market.dto.responses.ProductReviewsResponse;
import org.example.e_market.dto.responses.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(Long orderItemId, CreateReviewRequest request);
    ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request);
    void deleteReview(Long reviewId);
    ProductReviewsResponse getProductReviews(Long productId);
}
