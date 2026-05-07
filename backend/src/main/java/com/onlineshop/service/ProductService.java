package com.onlineshop.service;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onlineshop.dto.product.ProductListResponse;
import com.onlineshop.dto.product.ProductResponse;
import com.onlineshop.mapper.ProductMapper;
import com.onlineshop.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductListResponse getAllProducts(Pageable pageable) {
        return productMapper.toListResponse(productRepository.findAllActive(pageable));
    }

    public ProductListResponse getProductsByCategory(String categorySlug, Pageable pageable) {
        return productMapper.toListResponse(productRepository.findByCategorySlug(categorySlug, pageable));
    }

    public ProductListResponse searchProducts(String query, Pageable pageable) {
        return productMapper.toListResponse(productRepository.search(query, pageable));
    }

    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findActiveById(id).map(productMapper::toResponse);
    }

    public ProductListResponse getProducts(String categorySlug, String query, Pageable pageable) {
        boolean hasCategory = categorySlug != null && !categorySlug.isBlank();
        boolean hasQuery = query != null && !query.isBlank();

        if (hasCategory && hasQuery) {
            return productMapper.toListResponse(
                    productRepository.searchByCategorySlug(categorySlug.trim(), query.trim(), pageable));
        }

        if (hasQuery) {
            return productMapper.toListResponse(productRepository.search(query.trim(), pageable));
        }

        if (hasCategory) {
            return productMapper.toListResponse(productRepository.findByCategorySlug(categorySlug, pageable));
        }

        return productMapper.toListResponse(productRepository.findAllActive(pageable));
    }

}