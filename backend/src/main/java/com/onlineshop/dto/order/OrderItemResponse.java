package com.onlineshop.dto.order;

public record OrderItemResponse(
        Long productId,
        String sku,
        String productName,
        String gtin14,
        Integer unitPriceCents,
        Integer quantity,
        Integer lineTotalCents) {

}
