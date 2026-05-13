package org.example.e_market.repositories;

import org.example.e_market.entities.product.ProductImage;
import org.example.e_market.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
    List<ProductImage> findByProductAndIsPrimaryTrue(Product product);
}
