package org.example.e_market.mapper;

import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.dto.responses.VariantResponse;
import org.example.e_market.entities.product.Product;
import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.product.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product, List<ProductVariant> variants, List<ProductImage> images) {
        String primaryImage = images.stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .basePrice(product.getBasePrice())
                .productStatus(product.getProductStatus())
                .vendorId(product.getVendor() != null ? product.getVendor().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .variants(variants.stream().map(this::toVariantResponse).collect(Collectors.toList()))
                .primaryImageUrl(primaryImage)
                .averageRating(0.0)
                .build();
    }

    public VariantResponse toVariantResponse(ProductVariant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .priceDecimal(variant.getPriceDecimal())
                .stockQuantity(variant.getStockQuantity())
                .reservedQuantity(variant.getReservedQuantity())
                .build();
    }
}
