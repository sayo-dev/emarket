package org.example.e_market.repositories;

import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.dto.MonthlyRevenueProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByVendor(Vendor vendor);
    List<OrderItem> findByVendorAndItemStatus(Vendor vendor, OrderItemStatus status);

    @Query(value = "WITH RevenueCTE AS (" +
            "    SELECT " +
            "        TO_CHAR(oi.created_at, 'YYYY-MM') AS month, " +
            "        SUM(oi.unit_price * oi.quantity) AS revenue, " +
            "        SUM(oi.unit_price * oi.quantity) * (SELECT commission_rate_percent / 100 FROM platform_config WHERE id = 1) AS commission " +
            "    FROM order_items oi " +
            "    WHERE oi.vendor_id = :vendorId AND oi.item_status = 'DELIVERED' " +
            "    GROUP BY month " +
            "), " +
            "PayoutCTE AS (" +
            "    SELECT " +
            "        TO_CHAR(created_at, 'YYYY-MM') AS month, " +
            "        SUM(amount) AS payout " +
            "    FROM vendor_payouts " +
            "    WHERE vendor_id = :vendorId AND status = 'PROCESSED' " +
            "    GROUP BY month " +
            ") " +
            "SELECT " +
            "    COALESCE(r.month, p.month) AS month, " +
            "    COALESCE(r.revenue, 0) AS revenue, " +
            "    COALESCE(r.commission, 0) AS commission, " +
            "    COALESCE(p.payout, 0) AS payout " +
            "FROM RevenueCTE r " +
            "FULL OUTER JOIN PayoutCTE p ON r.month = p.month " +
            "ORDER BY month", nativeQuery = true)
    List<MonthlyRevenueProjection> getVendorRevenueReport(@Param("vendorId") String vendorId);
}
