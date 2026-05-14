package org.example.e_market.services;

import org.example.e_market.dto.responses.OrderResponse;
import org.example.e_market.dto.responses.OrderItemResponse;
import org.example.e_market.entities.enums.OrderItemStatus;

import java.util.List;

public interface OrderService {

    List<OrderResponse> getCustomerOrders();

    void markOrderAsPaid(Long orderId);

    List<OrderItemResponse> getVendorOrderItems(OrderItemStatus status);

    void markItemAsShipped(Long itemId, String trackingNumber, String courier);

    void markItemAsDelivered(Long itemId);

    void requestCancellation(Long orderId);

    List<OrderResponse> getOverdueOrders();

    void cancelOverdueOrders();
}
