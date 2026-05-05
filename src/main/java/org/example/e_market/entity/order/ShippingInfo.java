package org.example.e_market.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
