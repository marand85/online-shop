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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductListResponse> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String q) {

        if (q != null && !q.trim().isEmpty()) {
            return ResponseEntity.ok(productService.searchProducts(q.trim(), pageable));
        }

        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            return ResponseEntity.ok(productService.getProductsByCategory(categorySlug, pageable));
        }

        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
