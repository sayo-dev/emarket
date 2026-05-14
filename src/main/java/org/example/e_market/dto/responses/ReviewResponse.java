package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private String customerName;
    private Integer rating;
    private String title;
    private String body;
    private boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
}
