package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.responses.OrderItemResponse;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor/order-items")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('VENDOR_ADMIN', 'VENDOR_STAFF') and @vendorSecurity.isActive()")
public class VendorOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemResponse>>> getVendorOrderItems(@RequestParam(required = false) OrderItemStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Order items fetched successfully", orderService.getVendorOrderItems(status)));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Void>> markItemAsShipped(@PathVariable Long id, @RequestParam String trackingNumber, @RequestParam String courier) {
        orderService.markItemAsShipped(id, trackingNumber, courier);
        return ResponseEntity.ok(ApiResponse.success("Item marked as shipped", null));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<Void>> markItemAsDelivered(@PathVariable Long id) {
        orderService.markItemAsDelivered(id);
        return ResponseEntity.ok(ApiResponse.success("Item marked as delivered", null));
    }
}
