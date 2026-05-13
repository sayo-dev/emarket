package org.example.e_market.repositories;

import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByVendor(Vendor vendor);
    List<OrderItem> findByVendorAndItemStatus(Vendor vendor, OrderItemStatus status);
}
