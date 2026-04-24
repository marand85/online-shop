package com.onlineshop.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onlineshop.dto.product.ProductListResponse;
import com.onlineshop.dto.product.ProductResponse;
import com.onlineshop.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Products", description = "Product browsing, filtering and search endpoints")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get products", description = "Returns paginated list of products. Supports filtering by category and text search.")
    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @Parameter(description = "Filter products by category slug") @RequestParam(required = false) String categorySlug,
            @Parameter(description = "Search term in product name or description") @RequestParam(required = false) String q) {

        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(productService.searchProducts(q.trim(), pageable));
        }

        if (categorySlug != null && !categorySlug.isBlank()) {
            return ResponseEntity.ok(productService.getProductsByCategory(categorySlug.trim(), pageable));
        }

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @Operation(summary = "Get product by ID", description = "Returns detailed information about single product")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID of the product to retrieve") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
