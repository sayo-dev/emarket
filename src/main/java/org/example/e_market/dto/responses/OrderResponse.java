package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;
import org.example.e_market.entities.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class OrderResponse {
    Long id;
    String customerEmail;
    OrderStatus status;
    BigDecimal subtotal;
    BigDecimal platformCommission;
    BigDecimal vendorEarnings;
    BigDecimal shippingFee;
    BigDecimal total;
    List<OrderItemResponse> items;
    ShippingInfoResponse shippingInfo;
}
