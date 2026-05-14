package org.example.e_market.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.RaiseDisputeRequest;
import org.example.e_market.dto.requests.ResolveDisputeRequest;
import org.example.e_market.entities.Dispute;
import org.example.e_market.services.DisputeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Dispute>> raiseDispute(
            @PathVariable Long orderId,
            @Valid @RequestBody RaiseDisputeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dispute raised successfully", disputeService.raiseDispute(orderId, request)));
    }

    @PutMapping("/{disputeId}/resolve")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<Dispute>> resolveDispute(
            @PathVariable Long disputeId,
            @Valid @RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Dispute resolved successfully", disputeService.resolveDispute(disputeId, request)));
    }

    @GetMapping("/open")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<List<Dispute>>> getOpenDisputes() {
        return ResponseEntity.ok(ApiResponse.success("Open disputes fetched successfully", disputeService.getOpenDisputes()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<Dispute>>> getCustomerDisputes() {
        return ResponseEntity.ok(ApiResponse.success("Your disputes fetched successfully", disputeService.getCustomerDisputes()));
    }
}
