package org.example.e_market.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.e_market.services.CartService;
import org.example.e_market.services.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupScheduler {

    private final OrderService orderService;
    private final CartService cartService;

    @Scheduled(cron = "0 */5 * * * *") // 5 minutes
    public void cleanUpPendingOrders() {
        log.info("Running scheduled job: cleanUpPendingOrders");
        orderService.cancelOverdueOrders();
    }

    @Scheduled(cron = "0 0 0 * * *") // midnight(saily)
    public void cleanUpAbandonedCarts() {
        log.info("Running scheduled job: cleanUpAbandonedCarts");
        cartService.markAbandonedCarts();
    }
}
