package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.entities.PlatformConfig;
import org.example.e_market.entities.enums.VendorPayoutStatus;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.vendor.VendorPayout;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.PlatformConfigRepository;
import org.example.e_market.repositories.VendorPayoutRepository;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.services.AuditLogService;
import org.example.e_market.services.PayoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {

    private final VendorRepository vendorRepository;
    private final VendorPayoutRepository vendorPayoutRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public List<VendorPayout> triggerPayouts() {
        PlatformConfig config = platformConfigRepository.findById(1)
                .orElseGet(() -> PlatformConfig.builder()
                        .minPayoutThreshold(BigDecimal.valueOf(2000.00))
                        .build());

        BigDecimal threshold = config.getMinPayoutThreshold();

        List<Vendor> vendors = vendorRepository.findAll().stream()
                .filter(v -> v.getAvailablePayoutBalance() != null &&
                        v.getAvailablePayoutBalance().compareTo(threshold) >= 0)
                .toList();

        List<VendorPayout> payouts = new ArrayList<>();

        for (Vendor vendor : vendors) {
            BigDecimal amount = vendor.getAvailablePayoutBalance();

            VendorPayout payout = VendorPayout.builder()
                    .vendor(vendor)
                    .amount(amount)
                    .status(VendorPayoutStatus.PROCESSING)
                    .reference(UUID.randomUUID().toString())
                    .build();

            vendor.setAvailablePayoutBalance(BigDecimal.ZERO);
            vendorRepository.save(vendor);

            payout = vendorPayoutRepository.save(payout);
            payouts.add(payout);

            auditLogService.log("TRIGGER_PAYOUT", "VendorPayout", payout.getId(), "{\"amount\":" + amount + "}");
            log.info("Triggered payout for vendor {} amount {}", vendor.getId(), amount);
        }

        return payouts;
    }

    @Override
    @Transactional
    public VendorPayout updatePayoutStatus(Long payoutId, VendorPayoutStatus status) {
        VendorPayout payout = vendorPayoutRepository.findById(payoutId)
                .orElseThrow(() -> new CustomNotFoundException("Payout record not found"));

        payout.setStatus(status);
        if (status == VendorPayoutStatus.COMPLETED || status == VendorPayoutStatus.FAILED) {
            payout.setProcessedAt(LocalDateTime.now());
        }

        payout = vendorPayoutRepository.save(payout);
        auditLogService.log("UPDATE_PAYOUT_STATUS", "VendorPayout", payoutId, "{\"status\":\"" + status + "\"}");

        return payout;
    }
}
