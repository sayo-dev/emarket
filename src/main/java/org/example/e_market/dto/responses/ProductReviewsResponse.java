package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductReviewsResponse {
    private Double averageRating;
    private List<ReviewResponse> reviews;
}
