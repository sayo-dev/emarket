package org.example.e_market.entities.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "product_images")
public class ProductImage extends AbstractEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    private String imageUrl;

    private boolean isPrimary = false;

    private Integer displayOrder;
}