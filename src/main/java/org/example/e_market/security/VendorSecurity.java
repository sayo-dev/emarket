package org.example.e_market.security;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.entities.enums.VendorStatus;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.springframework.stereotype.Component;

@Component("vendorSecurity")
@RequiredArgsConstructor
public class VendorSecurity {

    private final CurrentUserUtil currentUserUtil;

    public boolean isActive() {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (vendor == null || vendor.getStatus() != VendorStatus.ACTIVE) {
            throw new CustomBadRequestException("Vendor not approved");
        }
        return true;
    }
}
