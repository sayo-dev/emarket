package org.example.e_market.utils.specifications;

import jakarta.persistence.criteria.Predicate;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.utils.filters.VendorFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class VendorSpecification {

    public static Specification<Vendor> filter(VendorFilter vendorFilter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (vendorFilter.getBusinessName() != null) {
                predicates.add(cb.like(cb.lower(root.get("businessName")), "%" + vendorFilter.getBusinessName().toLowerCase() + "%"));
            }

            if (vendorFilter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), vendorFilter.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };


    }
}
