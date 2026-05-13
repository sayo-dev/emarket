package org.example.e_market.services;

import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.requests.CreateProductRequest;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.utils.filters.ProductFilter;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    void addImage(Long productId, MultipartFile file, boolean isPrimary);

    void removeImage(Long imageId);

    void publishProduct(Long productId);

    void updateStock(Long variantId, Integer quantity, String reason);

    void softDeleteProduct(Long productId);

    PageResponse<ProductResponse> searchProducts(ProductFilter filter, int page, int size, String sortBy,
            String sortDir);

    ProductResponse getProductDetail(Long productId);
}
