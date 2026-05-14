package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CartItemResponse {
    private Long id;
    private String variantName;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal total;
}
