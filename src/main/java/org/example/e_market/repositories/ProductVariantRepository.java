package org.example.e_market.repositories;

import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProduct(Product product);

    Optional<ProductVariant> findBySku(String sku);
}
