package org.example.e_market.services;

import org.example.e_market.entities.vendor.VendorPayout;
import org.example.e_market.entities.enums.VendorPayoutStatus;

import java.util.List;

public interface PayoutService {
    List<VendorPayout> triggerPayouts();
    VendorPayout updatePayoutStatus(Long payoutId, VendorPayoutStatus status);
}
