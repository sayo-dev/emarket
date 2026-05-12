package org.example.e_market.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;
import org.example.e_market.entities.Category;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.enums.ProductStatus;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "products")
public class Product extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.DRAFT;

}
