package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.requests.CreateReviewRequest;
import org.example.e_market.dto.requests.UpdateReviewRequest;
import org.example.e_market.dto.responses.ProductReviewsResponse;
import org.example.e_market.dto.responses.ReviewResponse;
import org.example.e_market.entities.Review;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.product.Product;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomConflictException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.OrderItemRepository;
import org.example.e_market.repositories.ProductRepository;
import org.example.e_market.repositories.ReviewRepository;
import org.example.e_market.services.AuditLogService;
import org.example.e_market.services.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserUtil currentUserUtil;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ReviewResponse createReview(Long orderItemId, CreateReviewRequest request) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new CustomNotFoundException("Order item not found"));

        User customer = currentUserUtil.getCurrentUser();

        if (!orderItem.getOrder().getUser().getId().equals(customer.getId())) {
            throw new CustomBadRequestException("You can only review items you purchased");
        }

        if (orderItem.getItemStatus() != OrderItemStatus.DELIVERED) {
            throw new CustomBadRequestException("You can only review a product after it is delivered");
        }

        Product product = orderItem.getProductVariant().getProduct();

        if (reviewRepository.findByCustomerAndProduct(customer, product).isPresent()) {
            throw new CustomConflictException("You have already reviewed this product");
        }

        Review review = Review.builder()
                .product(product)
                .customer(customer)
                .orderItem(orderItem)
                .rating(request.rating())
                .title(request.title())
                .body(request.body())
                .isVerifiedPurchase(true)
                .build();

        review = reviewRepository.save(review);
        auditLogService.log("CREATE_REVIEW", "Review", review.getId(), null);

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomNotFoundException("Review not found"));

        User customer = currentUserUtil.getCurrentUser();

        if (!review.getCustomer().getId().equals(customer.getId())) {
            throw new CustomBadRequestException("You can only update your own reviews");
        }

        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setBody(request.body());

        review = reviewRepository.save(review);
        auditLogService.log("UPDATE_REVIEW", "Review", review.getId(), null);

        return mapToResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomNotFoundException("Review not found"));

        User customer = currentUserUtil.getCurrentUser();

        if (!review.getCustomer().getId().equals(customer.getId())) {
            throw new CustomBadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
        auditLogService.log("DELETE_REVIEW", "Review", reviewId, null);
    }

    @Override
    public ProductReviewsResponse getProductReviews(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));

        List<Review> reviews = reviewRepository.findByProduct(product);
        Double averageRating = reviewRepository.getAverageRating(product);

        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ProductReviewsResponse.builder()
                .averageRating(averageRating != null ? averageRating : 0.0)
                .reviews(reviewResponses)
                .build();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .customerName(review.getCustomer().getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .body(review.getBody())
                .isVerifiedPurchase(review.isVerifiedPurchase())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
