package org.example.e_market.repositories;

import org.example.e_market.entities.order.ShippingInfo;
import org.example.e_market.entities.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {
    Optional<ShippingInfo> findByOrder(Order order);
}
