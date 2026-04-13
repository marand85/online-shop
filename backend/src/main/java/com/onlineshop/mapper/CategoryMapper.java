package com.onlineshop.mapper;

import com.onlineshop.dto.category.CategoryResponse;
import com.onlineshop.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}