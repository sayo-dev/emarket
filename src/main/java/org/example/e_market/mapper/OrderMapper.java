package org.example.e_market.mapper;

import org.example.e_market.dto.responses.OrderResponse;
import org.example.e_market.dto.responses.OrderItemResponse;
import org.example.e_market.dto.responses.ShippingInfoResponse;
import org.example.e_market.entities.order.Order;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.order.ShippingInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .platformCommission(order.getPlatformCommission())
                .vendorEarnings(order.getVendorEarnings())
                .shippingFee(order.getShippingFee())
                .total(order.getTotal())
                .items(order.getItems().stream().map(this::toOrderItemResponse).collect(Collectors.toList()))
                .shippingInfo(toShippingInfoResponse(order.getShippingInfo()))
                .build();
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductVariant() != null ? item.getProductVariant().getId() : null)
                .productName(item.getProductVariant() != null ? item.getProductVariant().getName() : null)
                .sku(item.getProductVariant() != null ? item.getProductVariant().getSku() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .itemStatus(item.getItemStatus())
                .shippedAt(item.getShippedAt())
                .deliveredAt(item.getDeliveredAt())
                .build();
    }

    public ShippingInfoResponse toShippingInfoResponse(ShippingInfo info) {
        if (info == null) return null;
        return ShippingInfoResponse.builder()
                .recipientName(info.getRecipientName())
                .address(info.getAddress())
                .city(info.getCity())
                .state(info.getState())
                .postalCode(info.getPostalCode())
                .trackingNumber(info.getTrackingNumber())
                .courier(info.getCourier())
                .estimatedDeliveryAt(info.getEstimatedDeliveryAt())
                .actualDeliveryAt(info.getActualDeliveryAt())
                .build();
    }
}
