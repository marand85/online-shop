package com.onlineshop.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderEventListener {

    @EventListener
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Order placed: orderNumber={}, contactEmail={}, totalCents={}, currency={}, placedAt={}",
                event.orderNumber(),
                event.contactEmail(),
                event.totalCents(),
                event.currency(),
                event.placedAt());
    }
}
