package com.onlineshop.controller;

import com.onlineshop.BaseIntegrationTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("OrderController Integration Tests")
class OrderControllerIT extends BaseIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JdbcTemplate jdbcTemplate;

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

  @Test
  @DisplayName("Should return 400 when currency or country format is invalid")
  void shouldReturn400WhenCurrencyOrCountryFormatIsInvalid() throws Exception {
    String invalidJson = """
        {
          "items": [{"productId": 5, "quantity": 1}],
          "shipping": {
            "name": "Jan Kowalski",
            "line1": "ul. Testowa 1",
            "city": "Warszawa",
            "postal": "00-001",
            "country": "pl"
          },
          "contactEmail": "jan.kowalski@test.pl",
          "contactPhone": "123456789",
          "currency": "pln"
        }
        """;

    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Validation Failed"))
        .andExpect(jsonPath("$.errors.currency").value("currency must be a 3-letter uppercase ISO code"))
        .andExpect(jsonPath("$.errors['shipping.country']").value("country must be a 2-letter uppercase ISO code"));
  }

  @Test
  @DisplayName("Should return 409 when ordering inactive product")
  void shouldReturn409WhenProductIsInactive() throws Exception {
    long inactiveProductId = 9L;
    jdbcTemplate.update("UPDATE products SET active = false WHERE id = ?", inactiveProductId);

    try {
      String jsonRequest = """
          {
            "items": [{"productId": %d, "quantity": 1}],
            "shipping": {
              "name": "Jan Kowalski",
              "line1": "ul. Testowa 1",
              "city": "Warszawa",
              "postal": "00-001",
              "country": "PL"
            },
            "contactEmail": "jan.kowalski@test.pl",
            "contactPhone": "123456789",
            "currency": "PLN"
          }
          """.formatted(inactiveProductId);
      mockMvc.perform(post("/api/orders")
          .contentType(MediaType.APPLICATION_JSON)
          .content(jsonRequest))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.title").value("Product Unavailable"));
    } finally {
      jdbcTemplate.update("UPDATE products SET active = true WHERE id = ?", inactiveProductId);
    }
  }
}
