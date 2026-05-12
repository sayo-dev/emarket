package org.example.e_market.entities.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User user;

    private OrderStatus status = OrderStatus.PENDING_PAYMENT;

    private BigDecimal subtotal;

    private BigDecimal platformCommission;

    private BigDecimal vendorEarnings;

    private BigDecimal shippingFee;

    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private ShippingInfo shippingInfo;

}
