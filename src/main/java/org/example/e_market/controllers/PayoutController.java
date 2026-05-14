package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.entities.enums.VendorPayoutStatus;
import org.example.e_market.entities.vendor.VendorPayout;
import org.example.e_market.services.PayoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class PayoutController {

    private final PayoutService payoutService;

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<List<VendorPayout>>> triggerPayouts() {
        return ResponseEntity.ok(ApiResponse.success("Payouts triggered successfully", payoutService.triggerPayouts()));
    }

    @PutMapping("/{payoutId}/status")
    public ResponseEntity<ApiResponse<VendorPayout>> updatePayoutStatus(
            @PathVariable Long payoutId,
            @RequestParam VendorPayoutStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Payout status updated successfully", payoutService.updatePayoutStatus(payoutId, status)));
    }
}
