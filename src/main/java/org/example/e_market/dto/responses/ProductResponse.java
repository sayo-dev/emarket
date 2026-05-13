package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;
import org.example.e_market.entities.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class ProductResponse {
    Long id;
    String name;
    String description;
    BigDecimal basePrice;
    ProductStatus productStatus;
    String vendorId;
    String categoryName;
    List<VariantResponse> variants;
    String primaryImageUrl;
    Double averageRating;
}
