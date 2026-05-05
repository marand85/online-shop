package com.onlineshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.onlineshop.dto.order.OrderCreateRequest;
import com.onlineshop.dto.order.OrderItemRequest;
import com.onlineshop.dto.order.OrderResponse;
import com.onlineshop.dto.order.ShippingAddressRequest;
import com.onlineshop.entity.Order;
import com.onlineshop.entity.Product;
import com.onlineshop.enums.OrderStatus;
import com.onlineshop.event.OrderPlacedEvent;
import com.onlineshop.exception.InsufficientStockException;
import com.onlineshop.exception.ProductUnavailableException;
import com.onlineshop.exception.ResourceNotFoundException;
import com.onlineshop.mapper.OrderMapper;
import com.onlineshop.repository.OrderRepository;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.util.OrderNumberGenerator;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Unit Tests")
public class OrderServiceTest {

        @Mock
        private OrderRepository orderRepository;
        @Mock
        private ProductRepository productRepository;
        @Mock
        private OrderMapper orderMapper;
        @Mock
        private OrderNumberGenerator orderNumberGenerator;
        @Mock
        private ApplicationEventPublisher eventPublisher;

        @InjectMocks
        private OrderService orderService;

        private Product product;
        private OrderCreateRequest validRequest;
        private Order savedOrder;
        private OrderResponse orderResponse;

        @BeforeEach
        void setUp() {
                product = Product.builder()
                                .id(1L)
                                .sku("TEST-SKU")
                                .name("Test Product")
                                .priceCents(10000)
                                .stockQty(10)
                                .active(true)
                                .build();

                ShippingAddressRequest shipping = new ShippingAddressRequest(
                                "Jan Kowalski",
                                "ul. Testowa 1",
                                null,
                                "Warszawa",
                                null,
                                "00-001",
                                "PL");

                OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

                validRequest = new OrderCreateRequest(List.of(itemRequest),
                                shipping,
                                "test@example.com",
                                "123456789",
                                "PLN");

                savedOrder = Order.builder()
                                .id(100L)
                                .orderNumber("ORD-TEST123")
                                .contactEmail("test@example.com")
                                .status(OrderStatus.NEW)
                                .totalCents(20000)
                                .currency("PLN")
                                .placedAt(Instant.now())
                                .shippingName("Jan Kowalski")
                                .shippingLine1("ul. Testowa 1")
                                .shippingCity("Warszawa")
                                .shippingPostal("00-001")
                                .shippingCountry("PL")
                                .build();

                orderResponse = new OrderResponse(
                                "ORD-TEST123", OrderStatus.NEW, 20000, "PLN", Instant.now(),
                                List.of(), "Jan Kowalski", "ul. Testowa 1", null,
                                "Warszawa", null, "00-001", "PL");

        }

        @Test
        @DisplayName("Should successfully create order and publish event")
        void shouldCreateOrderSuccessfully() {
                when(productRepository.findById(1L)).thenReturn(Optional.of(product));
                when(orderNumberGenerator.generateOrderNumber()).thenReturn("ORD-TEST123");
                when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
                when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

                OrderResponse result = orderService.createOrder(validRequest);

                assertThat(result).isNotNull();
                assertThat(result.orderNumber()).isEqualTo("ORD-TEST123");

                verify(productRepository).findById(1L);
                verify(orderRepository).save(any(Order.class));
                verify(eventPublisher).publishEvent(any(OrderPlacedEvent.class));
                verify(orderMapper).toResponse(any(Order.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when product does not exist")
        void shouldThrowExceptionWhenProductNotFound() {
                when(productRepository.findById(999L)).thenReturn(Optional.empty());

                OrderCreateRequest requestWithInvalidProduct = new OrderCreateRequest(
                                List.of(new OrderItemRequest(999L, 1)),
                                validRequest.shipping(),
                                validRequest.contactEmail(),
                                validRequest.contactPhone(),
                                validRequest.currency());

                assertThatThrownBy(() -> orderService.createOrder(requestWithInvalidProduct))
                                .isInstanceOf(ResourceNotFoundException.class)
                                .hasMessageContaining("Product with id 999 not found");
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when not enough stock")
        void shouldThrowExceptionWhenInsufficientStock() {
                Product lowStockProduct = Product.builder()
                                .id(1L)
                                .sku("LOW-STOCK")
                                .stockQty(1)
                                .active(true)
                                .build();

                when(productRepository.findById(1L)).thenReturn(Optional.of(lowStockProduct));

                assertThatThrownBy(() -> orderService.createOrder(validRequest))
                                .isInstanceOf(InsufficientStockException.class)
                                .hasMessageContaining("has only 1 units in stock, but 2 were requested");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when product is not active")
        void shouldThrowExceptionWhenProductIsNotActive() {
                Product inactiveProduct = Product.builder()
                                .id(1L)
                                .sku("INACTIVE")
                                .active(false)
                                .build();

                when(productRepository.findById(1L)).thenReturn(Optional.of(inactiveProduct));

                assertThatThrownBy(() -> orderService.createOrder(validRequest))
                                .isInstanceOf(ProductUnavailableException.class)
                                .hasMessageContaining("is not active");
        }
}
