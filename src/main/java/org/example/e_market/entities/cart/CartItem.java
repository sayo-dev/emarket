package org.example.e_market.entities.cart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;
import org.example.e_market.entities.product.ProductVariant;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "cart_items")
public class CartItem extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private org.example.e_market.entities.cart.Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductVariant productVariant;

    private Integer quantity;

    private BigDecimal unitPrice;
}
