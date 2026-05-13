package org.example.e_market.utils.specifications;

import jakarta.persistence.criteria.Predicate;
import org.example.e_market.entities.product.Product;
import org.example.e_market.utils.filters.ProductFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), filter.getMaxPrice()));
            }

            if (filter.getVendorId() != null) {
                predicates.add(cb.equal(root.get("vendor").get("id"), filter.getVendorId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
