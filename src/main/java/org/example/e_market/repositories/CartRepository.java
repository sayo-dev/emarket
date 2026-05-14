package org.example.e_market.repositories;

import org.example.e_market.entities.cart.Cart;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);
    List<Cart> findByStatusAndUpdatedAtBefore(CartStatus status, LocalDateTime dateTime);
}
