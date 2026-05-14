package org.example.e_market.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateVariantRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @NotNull @PositiveOrZero BigDecimal priceModifier,
        @NotNull @PositiveOrZero Integer stockQuantity,
        @NotNull @PositiveOrZero Integer reservedQuantity
) {
}
