package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class VariantResponse {
    Long id;
    String sku;
    String name;
    BigDecimal priceModifier;
    Integer stockQuantity;
    Integer reservedQuantity;
}
