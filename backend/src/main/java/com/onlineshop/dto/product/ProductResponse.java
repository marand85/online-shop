package com.onlineshop.dto.product;

public record ProductResponse(
                Long id,
                String name,
                String sku,
                String gtin14,
                String description,
                Integer priceCents,
                String currency,
                String imageUrl,
                String categorySlug,
                String categoryName,
                Boolean active) {
}