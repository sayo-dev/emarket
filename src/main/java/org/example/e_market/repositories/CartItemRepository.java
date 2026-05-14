package org.example.e_market.repositories;

import org.example.e_market.entities.cart.Cart;
import org.example.e_market.entities.cart.CartItem;
import org.example.e_market.entities.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductVariant(Cart cart, ProductVariant productVariant);
}
