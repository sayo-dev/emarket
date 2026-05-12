package org.example.e_market.entities.product;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "product_variants")
public class ProductVariant extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(unique = true)
    private String sku;

    private String name;

    private BigDecimal priceDecimal;

    private Integer stockQuantity;

    private Integer reservedQuantity = 0;
}
