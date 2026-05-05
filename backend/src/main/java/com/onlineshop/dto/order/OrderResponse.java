package com.onlineshop.dto.order;

import java.time.Instant;
import java.util.List;

import com.onlineshop.enums.OrderStatus;

public record OrderResponse(
                String orderNumber,
                OrderStatus status,
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
