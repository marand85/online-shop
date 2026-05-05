package com.onlineshop.controller;

import com.onlineshop.BaseIntegrationTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("OrderController Integration Tests")
class OrderControllerIT extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Should create order successfully and return 201 CREATED")
  void shouldCreateOrderSuccessfully() throws Exception {
    // given
    String jsonRequest = """
        {
          "items": [{"productId": 5, "quantity": 2}],
          "shipping": {
            "name": "Jan Kowalski",
            "line1": "ul. Marszałkowska 1",
            "city": "Warszawa",
            "postal": "00-001",
            "country": "PL"
          },
          "contactEmail": "jan.kowalski@test.pl",
          "contactPhone": "123456789",
          "currency": "PLN"
        }
        """;
    // when & then
    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderNumber").exists())
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.totalCents").value(179800))
        .andExpect(jsonPath("$.currency").value("PLN"));
  }

  @Test
  @DisplayName("Should return 404 when order does not exist")
  void shouldReturn404WhenOrderNotFound() throws Exception {
    mockMvc.perform(get("/api/orders/NON-EXISTENT-ORDER"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.title").value("Resource Not Found"))
        .andExpect(jsonPath("$.detail").value("Order with number NON-EXISTENT-ORDER not found"));
  }

  @Test
  @DisplayName("Should return 400 when validation fails")
  void shouldReturn400OnValidationError() throws Exception {
    String invalidJson = """
        {
          "items": [],
          "shipping": {
            "name": "Jan Kowalski",
            "line1": "ul. Testowa 1",
            "city": "Warszawa",
            "postal": "00-001",
            "country": "PL"
          },
          "contactEmail": "bad-email",
          "contactPhone": "123456789",
          "currency": "PLN"
        }
        """;

    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.errors.items").exists())
        .andExpect(jsonPath("$.errors.contactEmail").exists());

  }
}
