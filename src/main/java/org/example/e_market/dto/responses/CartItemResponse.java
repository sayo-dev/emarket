package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CartItemResponse {
    Long id;
    Long variantId;
    String variantName;
    String sku;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal total;
}
