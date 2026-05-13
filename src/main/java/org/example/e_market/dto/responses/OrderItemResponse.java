package org.example.e_market.dto.responses;

import lombok.Builder;
import lombok.Value;
import org.example.e_market.entities.enums.OrderItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class OrderItemResponse {
    Long id;
    Long productId;
    String productName;
    String sku;
    Integer quantity;
    BigDecimal unitPrice;
    OrderItemStatus itemStatus;
    LocalDateTime shippedAt;
    LocalDateTime deliveredAt;
}
