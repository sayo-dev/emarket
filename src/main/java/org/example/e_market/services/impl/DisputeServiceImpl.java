package org.example.e_market.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.common.CurrentUserUtil;
import org.example.e_market.dto.requests.RaiseDisputeRequest;
import org.example.e_market.dto.requests.ResolveDisputeRequest;
import org.example.e_market.entities.Dispute;
import org.example.e_market.entities.PlatformConfig;
import org.example.e_market.entities.User;
import org.example.e_market.entities.enums.DisputeStatus;
import org.example.e_market.entities.enums.DisputeResolutionType;
import org.example.e_market.entities.enums.OrderStatus;
import org.example.e_market.entities.order.Order;
import org.example.e_market.entities.order.OrderItem;
import org.example.e_market.entities.vendor.Vendor;
import org.example.e_market.exceptions.CustomBadRequestException;
import org.example.e_market.exceptions.CustomNotFoundException;
import org.example.e_market.repositories.DisputeRepository;
import org.example.e_market.repositories.OrderRepository;
import org.example.e_market.repositories.PlatformConfigRepository;
import org.example.e_market.repositories.VendorRepository;
import org.example.e_market.services.AuditLogService;
import org.example.e_market.services.DisputeService;
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
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;
    private final PlatformConfigRepository platformConfigRepository;
    private final CurrentUserUtil currentUserUtil;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Dispute raiseDispute(Long orderId, RaiseDisputeRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomNotFoundException("Order not found"));

        User customer = currentUserUtil.getCurrentUser();

        if (!order.getUser().getId().equals(customer.getId())) {
            throw new CustomBadRequestException("You can only raise disputes on your own orders");
        }

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.SHIPPED) {
            throw new CustomBadRequestException("You can only raise disputes on SHIPPED or DELIVERED orders");
        }

        Dispute dispute = Dispute.builder()
                .order(order)
                .user(customer)
                .reason(request.reason())
                .description(request.description())
                .status(DisputeStatus.OPEN)
                .build();

        dispute = disputeRepository.save(dispute);
        auditLogService.log("RAISE_DISPUTE", "Dispute", dispute.getId(), null);

        return dispute;
    }

    @Override
    @Transactional
    public Dispute resolveDispute(Long disputeId, ResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new CustomNotFoundException("Dispute not found"));

        if (dispute.getStatus() != DisputeStatus.OPEN) {
            throw new CustomBadRequestException("Dispute is already resolved");
        }

        User admin = currentUserUtil.getCurrentUser();

        if (request.resolutionType() == DisputeResolutionType.FOR_CUSTOMER) {
            dispute.setStatus(DisputeStatus.RESOLVED_FOR_CUSTOMER);
        } else {
            dispute.setStatus(DisputeStatus.RESOLVED_FOR_VENDOR);
        }
        dispute.setResolutionNotes(request.resolutionNotes());
        dispute.setResolvedBy(admin);
        dispute.setResolvedAt(LocalDateTime.now());

        if (request.resolutionType() == DisputeResolutionType.FOR_CUSTOMER) {
            Order order = dispute.getOrder();
            order.setStatus(OrderStatus.REFUNDED);
            orderRepository.save(order);

            PlatformConfig config = platformConfigRepository.findById(1)
                    .orElseGet(() -> PlatformConfig.builder().build());
            BigDecimal commissionRate = config.getCommissionRatePercent().divide(BigDecimal.valueOf(100));

            List<OrderItem> items = order.getItems();
            Map<Vendor, List<OrderItem>> byVendor = items.stream()
                    .collect(Collectors.groupingBy(OrderItem::getVendor));

            for (Map.Entry<Vendor, List<OrderItem>> entry : byVendor.entrySet()) {
                Vendor vendor = entry.getKey();
                BigDecimal subtotal = entry.getValue().stream()
                        .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal earnings = subtotal.multiply(BigDecimal.ONE.subtract(commissionRate));

                vendor.setAvailablePayoutBalance(vendor.getAvailablePayoutBalance().subtract(earnings));
                vendorRepository.save(vendor);
                log.info("Debited vendor {} by {} due to dispute resolution FOR_CUSTOMER", vendor.getId(), earnings);
            }
        }

        dispute = disputeRepository.save(dispute);
        auditLogService.log("RESOLVE_DISPUTE", "Dispute", dispute.getId(),
                "{\"type\":\"" + request.resolutionType().name() + "\"}");

        return dispute;
    }

    @Override
    public List<Dispute> getOpenDisputes() {
        return disputeRepository.findByStatus(DisputeStatus.OPEN);
    }

    @Override
    public List<Dispute> getCustomerDisputes() {
        User customer = currentUserUtil.getCurrentUser();
        return disputeRepository.findByUser(customer);
    }
}
