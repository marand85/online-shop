package com.onlineshop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlineshop.dto.category.CategoryResponse;
import com.onlineshop.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<List<CategoryResponse>> getAllCategories() {
		List<CategoryResponse> categories = categoryService.getAllCategories();
		return ResponseEntity.ok(categories);
	}

}
