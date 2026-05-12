package org.example.e_market.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.product.Product;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "reviews")
public class Review extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    private User customer;

    @OneToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

    private Integer rating;

    private String title;

    private String body;

    private boolean isVerifiedPurchase = true;

}
