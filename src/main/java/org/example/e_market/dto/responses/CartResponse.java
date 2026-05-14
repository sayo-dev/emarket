package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class CartResponse {
    Long id;
    List<CartItemResponse> items;
    BigDecimal subtotal;
}
