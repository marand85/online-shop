package com.onlineshop.dto.product;

import java.util.List;

public record ProductListResponse(
        List<ProductResponse> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isLast) {
}
