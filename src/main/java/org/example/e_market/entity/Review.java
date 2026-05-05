package org.example.e_market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.e_market.entity.order.OrderItem;
import org.example.e_market.entity.product.Product;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
