package org.example.e_market.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.entities.vendor.Vendor;
import org.springframework.stereotype.Service;

public interface ProvisioningService {

    void provisionVendor(final Vendor vendor);
}
