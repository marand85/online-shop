package com.onlineshop.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.onlineshop.BaseIntegrationTest;
import com.onlineshop.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryRepositoryIT extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldFindAllSeededCategories() {
        // given (the base is empty, but Flyway injected seed)

        // when
        List<Category> allCategories = categoryRepository.findAll();

        // then
        assertThat(allCategories).hasSize(8);
        assertThat(allCategories).extracting(Category::getSlug)
                .contains("electronics", "books", "home-kitchen");

    }

}
