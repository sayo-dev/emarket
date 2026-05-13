package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.requests.CreateProductRequest;
import org.example.e_market.dto.requests.CreateVariantRequest;
import org.example.e_market.dto.responses.ProductResponse;
import org.example.e_market.services.AuditLogService;
import org.example.e_market.entities.Category;
import org.example.e_market.entities.product.Product;
import org.example.e_market.entities.product.ProductImage;
import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.enums.ProductStatus;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.mapper.ProductMapper;
import org.example.e_market.repositories.*;
import org.example.e_market.services.CloudinaryService;
import org.example.e_market.services.ProductService;
import org.example.e_market.utils.filters.ProductFilter;
import org.example.e_market.utils.specifications.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;
    private final CurrentUserUtil currentUserUtil;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

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
                    .priceModifier(vr.priceModifier())
                    .stockQuantity(vr.stockQuantity())
                    .build();
            productVariantRepository.save(variant);
        }

        auditLogService.log("CREATE_PRODUCT", "Product", product.getId(), null);

        return productMapper.toResponse(product,
                productVariantRepository.findByProduct(product),
                productImageRepository.findByProduct(product));
    }

    @Override
    public void addImage(Long productId, MultipartFile file, boolean isPrimary) {
        Map uploadResult = upload(file);
        String imageUrl = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        try {
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
            auditLogService.log("ADD_IMAGE", "Product", productId, "{\"imageUrl\":\"" + imageUrl + "\"}");
        } catch (Exception e) {
            log.error("Failed to add image to product, deleting from Cloudinary", e);
            try {
                cloudinaryService.delete(publicId);
            } catch (IOException ioException) {
                log.error("Failed to delete image from Cloudinary after error", ioException);
            }
            throw e;
        }
    }

    @Override
    public void removeImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomNotFoundException("Image not found"));

        if (image.isPrimary()) {
            throw new CustomBadRequestException("Cannot remove primary image");
        }

        productImageRepository.delete(image);
        auditLogService.log("REMOVE_IMAGE", "Product", image.getProduct().getId(), "{\"imageId\":" + imageId + "}");
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
        auditLogService.log("PUBLISH_PRODUCT", "Product", productId, null);
    }

    @Override
    public void updateStock(Long variantId, Integer quantity, String reason) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new CustomNotFoundException("Variant not found"));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);
        productVariantRepository.save(variant);

        auditLogService.log("UPDATE_STOCK", "ProductVariant", variantId, "{\"reason\":\"" + reason + "\",\"quantity\":" + quantity + "}");
    }

    @Override
    public void softDeleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomNotFoundException("Product not found"));

        product.setDeleted(true);
        productRepository.save(product);
        auditLogService.log("DELETE_PRODUCT", "Product", productId, null);
    }

    @Override
    public PageResponse<ProductResponse> searchProducts(ProductFilter filter, int page, int size, String sortBy,
            String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Product> spec = ProductSpecification.filter(filter);

        Page<Product> result = productRepository.findAll(spec, pageable);
        return PageResponse.of(result.map(p -> productMapper.toResponse(p,
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

    private Map upload(MultipartFile file) {

        if (file.isEmpty() || !Objects.requireNonNull(file.getContentType()).startsWith("image/"))
            throw new IllegalArgumentException("File must not be empty and must a valid type.");

        try {
            return cloudinaryService.upload(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
