package org.example.e_market.services;

import org.example.e_market.dto.responses.CartResponse;

public interface CartService {
    void addItemToCart(Long variantId, Integer quantity);
    void updateItemQuantity(Long itemId, Integer quantity);
    void removeItemFromCart(Long itemId);
    CartResponse viewCart();
    void checkoutCart();
    void markAbandonedCarts();
}
