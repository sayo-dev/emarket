package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.services.ProductService;
import org.example.e_market.utils.filters.ProductFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> searchProducts(ProductFilter filter, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", productService.searchProducts(filter, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", productService.getProductDetail(id)));
    }
}
