package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.responses.CartResponse;
import org.example.e_market.dto.responses.CartItemResponse;
import org.example.e_market.entities.User;
import org.example.e_market.entities.cart.Cart;
import org.example.e_market.entities.cart.CartItem;
import org.example.e_market.entities.product.ProductVariant;
import org.example.e_market.entities.enums.CartStatus;
import org.example.e_market.entities.order.Order;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.enums.OrderStatus;
import org.example.e_market.entities.enums.OrderItemStatus;
import org.example.e_market.entities.PlatformConfig;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.*;
import org.example.e_market.services.CartService;
import org.example.e_market.services.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final CurrentUserUtil currentUserUtil;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public void addItemToCart(Long variantId, Integer quantity) {
        User user = currentUserUtil.getCurrentUser();
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().user(user).status(CartStatus.ACTIVE).items(new ArrayList<>()).build();
                    return cartRepository.save(newCart);
                });

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new CustomNotFoundException("Product variant not found"));

        int currentReserved = variant.getReservedQuantity() != null ? variant.getReservedQuantity() : 0;
        int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;

        if (currentStock - currentReserved < quantity) {
            throw new CustomBadRequestException("Insufficient stock available");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProductVariant(cart, variant);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            BigDecimal unitPrice = variant.getProduct().getBasePrice().add(
                    variant.getPriceModifier() != null ? variant.getPriceModifier() : BigDecimal.ZERO
            );
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variant)
                    .quantity(quantity)
                    .unitPrice(unitPrice)
                    .build();
            cartItemRepository.save(newItem);
        }
        auditLogService.log("ADD_TO_CART", "Cart", cart.getId(), "{\"variantId\":" + variantId + ",\"quantity\":" + quantity + "}");
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomNotFoundException("Cart item not found"));

        ProductVariant variant = productVariantRepository.findById(item.getProductVariant().getId())
                .orElseThrow(() -> new CustomNotFoundException("Product variant not found"));

        int currentReserved = variant.getReservedQuantity() != null ? variant.getReservedQuantity() : 0;
        int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;

        if (currentStock - currentReserved < quantity) {
            throw new CustomBadRequestException("Insufficient stock available");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        auditLogService.log("UPDATE_CART_ITEM", "CartItem", itemId, "{\"quantity\":" + quantity + "}");
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomNotFoundException("Cart item not found"));
        cartItemRepository.delete(item);
        auditLogService.log("REMOVE_FROM_CART", "CartItem", itemId, null);
    }

    @Override
    public CartResponse viewCart() {
        User user = currentUserUtil.getCurrentUser();
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new CustomNotFoundException("No active cart found"));

        List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            BigDecimal total = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            return CartItemResponse.builder()
                    .id(item.getId())
                    .price(item.getUnitPrice())
//                    .variantId(item.getProductVariant().getId())
                    .variantName(item.getProductVariant().getName())
                    .sku(item.getProductVariant().getSku())
                    .quantity(item.getQuantity())
//                    .unitPrice(item.getUnitPrice())
                    .total(total)
                    .build();
        }).collect(Collectors.toList());

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .subtotal(subtotal)
                .build();
    }

    @Override
    @Transactional
    public void checkoutCart() {
        User user = currentUserUtil.getCurrentUser();
        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new CustomNotFoundException("No active cart found"));

        if (cart.getItems().isEmpty()) {
            throw new CustomBadRequestException("Cart is empty");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .shippingFee(BigDecimal.ZERO)
                .build();

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = productVariantRepository.findById(cartItem.getProductVariant().getId())
                    .orElseThrow(() -> new CustomNotFoundException("Product variant not found"));

            int currentReserved = variant.getReservedQuantity() != null ? variant.getReservedQuantity() : 0;
            int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;

            if (currentStock - currentReserved < cartItem.getQuantity()) {
                throw new CustomBadRequestException("Insufficient stock for item: " + variant.getName());
            }

            variant.setReservedQuantity(currentReserved + cartItem.getQuantity());
            variant.setStockQuantity(currentStock - cartItem.getQuantity());
            productVariantRepository.save(variant);

            BigDecimal itemTotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .vendor(variant.getProduct().getVendor())
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .itemStatus(OrderItemStatus.PROCESSING)
                    .build();
            orderItems.add(orderItem);
        }

        PlatformConfig config = platformConfigRepository.findById(1)
                .orElseGet(() -> PlatformConfig.builder().build());
        BigDecimal commissionRate = config.getCommissionRatePercent().divide(BigDecimal.valueOf(100));
        BigDecimal platformCommission = subtotal.multiply(commissionRate);
        BigDecimal vendorEarnings = subtotal.subtract(platformCommission);

        order.setSubtotal(subtotal);
        order.setPlatformCommission(platformCommission);
        order.setVendorEarnings(vendorEarnings);
        order.setTotal(subtotal.add(order.getShippingFee()));
        order.setItems(orderItems);

        orderRepository.save(order);

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        auditLogService.log("CHECKOUT_CART", "Cart", cart.getId(), "{\"orderId\":" + order.getId() + "}");
    }

    @Override
    @Transactional
    public void markAbandonedCarts() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        List<Cart> carts = cartRepository.findByStatusAndUpdatedAtBefore(CartStatus.ACTIVE, twentyFourHoursAgo);

        for (Cart cart : carts) {
            cart.setStatus(CartStatus.ABANDONED);
            cartRepository.save(cart);
            auditLogService.log("ABANDON_CART", "Cart", cart.getId(), "{\"reason\":\"Inactive for 24h\"}");
        }
    }
}
