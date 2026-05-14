package org.example.e_market.controllers;

import lombok.RequiredArgsConstructor;
import org.example.e_market.common.ApiResponse;
import org.example.e_market.dto.requests.AddToCartRequest;
import org.example.e_market.dto.requests.UpdateCartItemRequest;
import org.example.e_market.dto.responses.CartResponse;
import org.example.e_market.services.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addItemToCart(@RequestBody AddToCartRequest request) {
        cartService.addItemToCart(request.getVariantId(), request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", null));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> updateItemQuantity(@PathVariable Long id, @RequestBody UpdateCartItemRequest request) {
        cartService.updateItemQuantity(id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", null));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> removeItemFromCart(@PathVariable Long id) {
        cartService.removeItemFromCart(id);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> viewCart() {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched successfully", cartService.viewCart()));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Void>> checkoutCart() {
        cartService.checkoutCart();
        return ResponseEntity.ok(ApiResponse.success("Cart checked out successfully", null));
    }
}
