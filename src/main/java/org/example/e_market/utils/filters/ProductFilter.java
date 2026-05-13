package org.example.e_market.utils.filters;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        Long categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String vendorId,
        Double minRating
) {
}
