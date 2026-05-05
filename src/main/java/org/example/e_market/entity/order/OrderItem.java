package org.example.e_market.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.e_market.entity.vendor.Vendor;
import org.example.e_market.entity.enums.OrderItemStatus;
import org.example.e_market.entity.product.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    private Integer quantity;

    private BigDecimal unitPrice;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus itemStatus = OrderItemStatus.PROCESSING;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;


}
