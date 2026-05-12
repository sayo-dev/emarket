package org.example.e_market.services;

import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.enums.PayoutSchedule;
import org.example.e_market.entities.enums.VendorStatus;
import org.example.e_market.utils.filters.VendorFilter;

import java.math.BigDecimal;
import java.util.UUID;

public interface AdminService {

    void approveVendor(final String vendorId);

    void suspendVendor(final String vendorId);

    void updateGlobalConfig(final BigDecimal rate, final BigDecimal threshold, final PayoutSchedule schedule);

    PageResponse<VendorResponse> getAllVendors(final VendorFilter filter, final int page, final int size, final String sortBy, final String sortDir);
}
