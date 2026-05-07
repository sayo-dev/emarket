package org.example.e_market.services.admin;

import org.example.e_market.dto.response.VendorResponse;
import org.example.e_market.entity.enums.VendorStatus;
import org.example.e_market.utils.filters.VendorFilter;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AdminService {

    void updateVendorStatus(UUID vendorId, VendorStatus status);

    Page<VendorResponse> getAllVendors(VendorFilter filter, int page, int size, String sortBy, String sortDir);
}
