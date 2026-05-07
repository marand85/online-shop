package com.onlineshop.controller;

import com.onlineshop.BaseIntegrationTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("ProductController Integration Tests")
public class ProductControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return 404 problem detail when product does not exist")
    void shouldReturn404WhenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Product with id 999999 not found"));
    }

    @Test
    @DisplayName("Should filter products by category and query together")
    void shouldFilterProductsByCategoryAndQueryTogether() throws Exception {
        mockMvc.perform(get("/api/products")
                .param("categorySlug", "accessories")
                .param("q", "backpack")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].sku").value("BACKPACK-TECH"))
                .andExpect(jsonPath("$.content[0].categorySlug").value("accessories"));
    }
}
