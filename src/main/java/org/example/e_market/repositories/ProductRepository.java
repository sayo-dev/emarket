package org.example.e_market.repositories;

import org.example.e_market.entities.product.Product;
import org.example.e_market.entities.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByVendor(Vendor vendor);
}
