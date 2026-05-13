package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.requests.CreateProductRequest;
import org.example.e_market.dto.requests.CreateVariantRequest;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.entities.AuditLog;
import org.example.e_market.entities.Category;
import org.example.e_market.entities.product.Product;
import org.example.e_market.entities.product.ProductImage;
import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.enums.ProductStatus;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.mapper.ProductMapper;
import org.example.e_market.repositories.*;
import org.example.e_market.services.ProductService;
import org.example.e_market.utils.filters.ProductFilter;
import org.example.e_market.utils.specifications.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final CurrentUserUtil currentUserUtil;
    private final ProductMapper productMapper;

    @Transactional
    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CustomNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .basePrice(request.basePrice())
                .category(category)
                .vendor(currentUserUtil.getCurrentUser().getVendor())
                .productStatus(ProductStatus.DRAFT)
                .build();

        product = productRepository.save(product);

        for (CreateVariantRequest vr : request.variants()) {
            ProductVariant variant = ProductVariant.builder()
                    .product(product)
                    .sku(vr.sku())
                    .name(vr.name())
                    .priceDecimal(vr.priceDecimal())
                    .stockQuantity(vr.stockQuantity())
                    .build();
            productVariantRepository.save(variant);
        }

        return productMapper.toResponse(product,
                productVariantRepository.findByProduct(product),
                productImageRepository.findByProduct(product));
    }

    @Override
    public void addImage(Long productId, String imageUrl, boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));

        if (isPrimary) {
            List<ProductImage> primaries = productImageRepository.findByProductAndIsPrimaryTrue(product);
            for (ProductImage img : primaries) {
                img.setPrimary(false);
                productImageRepository.save(img);
            }
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .isPrimary(isPrimary)
                .build();
        productImageRepository.save(image);
    }

    @Override
    public void removeImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomNotFoundException("Image not found"));

        if (image.isPrimary()) {
            throw new CustomBadRequestException("Cannot remove primary image");
        }

        productImageRepository.delete(image);
    }

    @Override
    public void publishProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));

        List<ProductVariant> variants = productVariantRepository.findByProduct(product);
        boolean hasStock = variants.stream().anyMatch(v -> v.getStockQuantity() > 0);

        if (!hasStock) {
            throw new CustomBadRequestException("Cannot publish product without stock");
        }

        product.setProductStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
    }

    @Override
    public void updateStock(Long variantId, Integer quantity, String reason) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new CustomNotFoundException("Variant not found"));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        productVariantRepository.save(variant);

        AuditLog auditLog = AuditLog.builder()
                .user(currentUserUtil.getCurrentUser())
                .action("UPDATE_STOCK")
                .entityType("ProductVariant")
                .entityId(variant.getId())
                .metadata("{\"reason\":\"" + reason + "\"}")
                .build();
        auditLogRepository.save(auditLog);
    }

    @Override
    public void softDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(ProductFilter filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filter(filter);

        Page<Product> page = productRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(p -> productMapper.toResponse(p,
                productVariantRepository.findByProduct(p),
                productImageRepository.findByProduct(p))));
    }

    @Override
    public ProductResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));
        return productMapper.toResponse(product,
                productVariantRepository.findByProduct(product),
                productImageRepository.findByProduct(product));
    }
}
