package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.responses.OrderResponse;
import org.example.e_market.dto.responses.OrderItemResponse;
import org.example.e_market.entities.PlatformConfig;
import org.example.e_market.entities.User;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.entities.order.Order;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.order.ShippingInfo;
import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.enums.OrderStatus;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.mapper.OrderMapper;
import org.example.e_market.repositories.*;
import org.example.e_market.services.OrderService;
import org.example.e_market.services.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShippingInfoRepository shippingInfoRepository;
    private final VendorRepository vendorRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CurrentUserUtil currentUserUtil;
    private final OrderMapper orderMapper;
    private final AuditLogService auditLogService;

    @Override
    public List<OrderResponse> getCustomerOrders() {
        User user = currentUserUtil.getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markOrderAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new CustomBadRequestException("Order is not in PENDING_PAYMENT status");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
        auditLogService.log("PAY_ORDER", "Order", orderId, null);
    }

    @Override
    public List<OrderItemResponse> getVendorOrderItems(OrderItemStatus status) {
        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        List<OrderItem> items;
        if (status != null) {
            items = orderItemRepository.findByVendorAndItemStatus(vendor, status);
        } else {
            items = orderItemRepository.findByVendor(vendor);
        }
        return items.stream().map(orderMapper::toOrderItemResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markItemAsShipped(Long itemId, String trackingNumber, String courier) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomNotFoundException("Order item not found"));

        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (!item.getVendor().getId().equals(vendor.getId())) {
            throw new CustomBadRequestException("You are not authorized to update this item");
        }

        item.setItemStatus(OrderItemStatus.SHIPPED);
        item.setShippedAt(LocalDateTime.now());
        orderItemRepository.save(item);

        Order order = item.getOrder();
        ShippingInfo shippingInfo = shippingInfoRepository.findByOrder(order)
                .orElseGet(() -> ShippingInfo.builder().order(order).build());

        shippingInfo.setCourier(courier);
        shippingInfo.setTrackingNumber(trackingNumber);
        shippingInfoRepository.save(shippingInfo);
        auditLogService.log("SHIP_ITEM", "OrderItem", itemId, "{\"trackingNumber\":\"" + trackingNumber + "\"}");
    }

    @Override
    @Transactional
    public void markItemAsDelivered(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomNotFoundException("Order item not found"));

        Vendor vendor = currentUserUtil.getCurrentUser().getVendor();
        if (!item.getVendor().getId().equals(vendor.getId())) {
            throw new CustomBadRequestException("You are not authorized to update this item");
        }

        item.setItemStatus(OrderItemStatus.DELIVERED);
        item.setDeliveredAt(LocalDateTime.now());
        orderItemRepository.save(item);
        auditLogService.log("DELIVER_ITEM", "OrderItem", itemId, null);

        Order order = item.getOrder();
        List<OrderItem> allItems = order.getItems();
        boolean allDelivered = allItems.stream().allMatch(i -> i.getItemStatus() == OrderItemStatus.DELIVERED);

        if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            auditLogService.log("DELIVER_ORDER", "Order", order.getId(), null);

            PlatformConfig config = platformConfigRepository.findById(1)
                    .orElseGet(() -> PlatformConfig.builder().build());
            BigDecimal commissionRate = config.getCommissionRatePercent().divide(BigDecimal.valueOf(100));

            Map<Vendor, List<OrderItem>> byVendor = allItems.stream().collect(Collectors.groupingBy(OrderItem::getVendor));

            for (Map.Entry<Vendor, List<OrderItem>> entry : byVendor.entrySet()) {
                Vendor v = entry.getKey();
                BigDecimal subtotal = entry.getValue().stream()
                        .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal earnings = subtotal.multiply(BigDecimal.ONE.subtract(commissionRate));

                v.setTotalEarnings(v.getTotalEarnings().add(earnings));
                v.setAvailablePayoutBalance(v.getAvailablePayoutBalance().add(earnings));
                vendorRepository.save(v);
            }
        }
    }

    @Override
    @Transactional
    public void requestCancellation(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("Order not found"));

        User user = currentUserUtil.getCurrentUser();
        if (!order.getUser().getId().equals(user.getId())) {
            throw new CustomBadRequestException("You are not authorized to cancel this order");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT && order.getStatus() != OrderStatus.PAID) {
            throw new CustomBadRequestException("Order cannot be cancelled in current status");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        auditLogService.log("CANCEL_ORDER", "Order", orderId, null);

        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getProductVariant();
            if (variant != null) {
                variant.setReservedQuantity(variant.getReservedQuantity() - item.getQuantity());
                variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                productVariantRepository.save(variant);
            }
        }
    }

    @Override
    public List<OrderResponse> getOverdueOrders() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PAID, threeDaysAgo);
        return orders.stream().map(orderMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelOverdueOrders() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING_PAYMENT, thirtyMinutesAgo);
        
        for (Order order : orders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            auditLogService.log("AUTO_CANCEL_ORDER", "Order", order.getId(), "{\"reason\":\"Payment overdue\"}");
            
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getProductVariant();
                if (variant != null) {
                    variant.setReservedQuantity(variant.getReservedQuantity() - item.getQuantity());
                    variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
                    productVariantRepository.save(variant);
                }
            }
        }
    }
}
