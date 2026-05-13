package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.responses.OrderResponse;
import org.example.e_market.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getCustomerOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orderService.getCustomerOrders()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> requestCancellation(@PathVariable Long id) {
        orderService.requestCancellation(id);
        return ResponseEntity.ok(ApiResponse.success("Cancellation requested successfully", null));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Void>> markOrderAsPaid(@PathVariable Long id) {
        orderService.markOrderAsPaid(id);
        return ResponseEntity.ok(ApiResponse.success("Order marked as paid", null));
    }
}
