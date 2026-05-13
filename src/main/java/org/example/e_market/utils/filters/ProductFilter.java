package org.example.e_market.utils.filters;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ProductFilter {
    String name;
    Long categoryId;
    BigDecimal minPrice;
    BigDecimal maxPrice;
    String vendorId;
    Double minRating;

}
