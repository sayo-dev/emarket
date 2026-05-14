package org.example.e_market.dto.requests;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long variantId;
    private Integer quantity;
}
