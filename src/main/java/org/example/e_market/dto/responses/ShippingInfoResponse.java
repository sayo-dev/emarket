package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ShippingInfoResponse {
    String recipientName;
    String address;
    String city;
    String state;
    String postalCode;
    String trackingNumber;
    String courier;
    LocalDateTime estimatedDeliveryAt;
    LocalDateTime actualDeliveryAt;
}
