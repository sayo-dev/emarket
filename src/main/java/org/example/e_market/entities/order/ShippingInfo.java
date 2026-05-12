package org.example.e_market.entities.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.e_market.entities.AbstractEntity;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
public class ShippingInfo extends AbstractEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    private String recipientName;
    private String address;
    private String city;
    private String state;
    private String postalCode;

    private String trackingNumber;
    private String courier;

    private LocalDateTime estimatedDeliveryAt;
    private LocalDateTime actualDeliveryAt;

}
