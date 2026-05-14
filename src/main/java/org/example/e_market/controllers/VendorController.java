package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.UpdateVendorProfileRequest;
import org.example.e_market.dto.responses.PayoutResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.dto.VendorRevenueReportDto;
import org.example.e_market.services.VendorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('VENDOR_ADMIN')")
public class VendorController {

    private final VendorService vendorService;

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateStoreProfile(
            @Valid @RequestBody UpdateVendorProfileRequest request) {
        vendorService.updateStoreProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Store profile updated successfully", null));
    }

    @GetMapping("/earnings")
    public ResponseEntity<ApiResponse<VendorResponse>> getEarningsSummary() {
        return ResponseEntity
                .ok(ApiResponse.success("Earnings summary fetched successfully", vendorService.getEarningsSummary()));
    }

    @GetMapping("/payouts")
    public ResponseEntity<ApiResponse<List<PayoutResponse>>> getPayoutHistory() {
        return ResponseEntity
                .ok(ApiResponse.success("Payout history fetched successfully", vendorService.getPayoutHistory()));
    }

    @PostMapping("/invite")
    public ResponseEntity<ApiResponse<Void>> inviteVendorUser(@RequestParam String email) {
        vendorService.inviteStaff(email);
        return ResponseEntity.ok(ApiResponse.success("User invited successfully", null));
    }

    @GetMapping("/revenue-report")
    public ResponseEntity<ApiResponse<VendorRevenueReportDto>> getRevenueReport() {
        return ResponseEntity
                .ok(ApiResponse.success("Revenue report fetched successfully", vendorService.getRevenueReport()));
    }
}
