package org.example.e_market.entities.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.entities.product.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "order_items")
public class OrderItem extends AbstractEntity {

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
