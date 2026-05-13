package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.CreateProductRequest;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vendor/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_STAFF') and @vendorSecurity.isActive()")
public class VendorProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success("Product created successfully", productService.createProduct(request)));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> addImage(@PathVariable Long id, @RequestParam("file") MultipartFile file,
            @RequestParam boolean isPrimary) {
        productService.addImage(id, file, isPrimary);
        return ResponseEntity.ok(ApiResponse.success("Image added successfully", null));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> removeImage(@PathVariable Long imageId) {
        productService.removeImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image removed successfully", null));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<Void>> publishProduct(@PathVariable Long id) {
        productService.publishProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product published successfully", null));
    }

    @PutMapping("/variants/{variantId}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable Long variantId, @RequestParam Integer quantity,
            @RequestParam(required = false) String reason) {
        productService.updateStock(variantId, quantity, reason);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> softDeleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
