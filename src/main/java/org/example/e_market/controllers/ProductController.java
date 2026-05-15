package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.entities.enums.VendorStatus;
import org.example.e_market.services.ProductService;
import org.example.e_market.utils.filters.ProductFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(@RequestParam(required = false) String name,
                                                                                     @RequestParam(required = false) Long categoryId,
                                                                                     @RequestParam(required = false) BigDecimal minPrice,
                                                                                     @RequestParam(required = false) BigDecimal maxPrice,
                                                                                     @RequestParam(required = false) String vendorId,
                                                                                     @RequestParam(required = false) Double minRating,
                                                                                     @RequestParam(defaultValue = "1") int page,
                                                                                     @RequestParam(defaultValue = "10") int size,
                                                                                     @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                                     @RequestParam(defaultValue = "DESC") String sortDir) {
        final ProductFilter filter = ProductFilter.builder()
                .name(name).categoryId(categoryId).minPrice(minPrice).maxPrice(maxPrice).vendorId(vendorId).minRating(minRating).build();

        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", productService.searchProducts(filter, page, size, sortBy, sortDir)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", productService.getProductDetail(id)));
    }
}
