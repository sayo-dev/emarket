package org.example.e_market.repositories;

import org.example.e_market.entities.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, String>, JpaSpecificationExecutor<Vendor> {

    @Query("SELECT EXISTS (SELECT 1 FROM Vendor v WHERE lower(v.businessName) = lower(:name) OR lower(v.businessEmail) = lower(:email)) AS DOES_EXIST")
    boolean existsByNameOrEmail(String name, String email);
}
