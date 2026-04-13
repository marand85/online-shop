package com.onlineshop.mapper;

import com.onlineshop.dto.product.ProductResponse;
import com.onlineshop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.slug", target = "categorySlug")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);
}