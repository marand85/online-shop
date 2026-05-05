package com.onlineshop.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onlineshop.dto.category.CategoryResponse;
import com.onlineshop.mapper.CategoryMapper;
import com.onlineshop.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

}
