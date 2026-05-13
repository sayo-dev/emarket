package org.example.e_market.repositories;

import org.example.e_market.entities.vendor.VendorPayout;
import org.example.e_market.entities.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorPayoutRepository extends JpaRepository<VendorPayout, Long> {
    List<VendorPayout> findByVendor(Vendor vendor);
}
