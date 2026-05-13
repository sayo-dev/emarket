package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.PageResponse;
import org.example.e_market.dto.responses.VendorResponse;
import org.example.e_market.entities.enums.VendorStatus;
import org.example.e_market.services.AdminService;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.utils.filters.VendorFilter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminController {

    private final AdminService adminService;


    @PatchMapping("/vendors/approve/{vendorId}")
    public ResponseEntity<ApiResponse<String>> approveVendor(@PathVariable String vendorId) {

        adminService.approveVendor(vendorId);
        return ResponseEntity.ok(ApiResponse.success("Vendor approved successfully", null));
    }

    @PatchMapping("/vendors/suspend/{vendorId}")
    public ResponseEntity<ApiResponse<String>> suspendVendor(@PathVariable String vendorId) {

        adminService.suspendVendor(vendorId);
        return ResponseEntity.ok(ApiResponse.success("Vendor suspended successfully", null));
    }

    @GetMapping("/vendors/all")
    public ResponseEntity<ApiResponse<PageResponse<VendorResponse>>> getAllVendors(

            @RequestParam(required = false) String businessName,
            @RequestParam(required = false) VendorStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        VendorFilter vendorFilter = VendorFilter.builder()
                .businessName(businessName)
                .status(status)
                .build();

        PageResponse<VendorResponse> allVendors = adminService.getAllVendors(vendorFilter, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Request successful", allVendors));
    }


}
