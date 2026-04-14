package com.onlineshop.mapper;

import com.onlineshop.dto.product.ProductListResponse;
import com.onlineshop.dto.product.ProductResponse;
import com.onlineshop.entity.Product;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.slug", target = "categorySlug")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    default ProductListResponse toListResponse(Page<Product> productPage) {
        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new ProductListResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast());
    }
}