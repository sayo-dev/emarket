package org.example.e_market.entity.product;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(unique = true)
    private String sku;

    private String name;

    private BigDecimal priceDecimal;

    private Integer stockQuantity;

    private Integer reservedQuantity = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
