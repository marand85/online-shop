package com.onlineshop.dto.order;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String orderNumber,
        String status,
        Integer totalCents,
        String currency,
        Instant placedAt,
        List<OrderItemResponse> items,
        String shippingName,
        String shippingLine1,
        String shippingLine2,
        String shippingCity,
        String shippingState,
        String shippingPostal,
        String shippingCountry) {
}
