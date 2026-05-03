package com.onlineshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onlineshop.dto.order.OrderCreateRequest;
import com.onlineshop.dto.order.OrderResponse;
import com.onlineshop.entity.Order;
import com.onlineshop.entity.OrderItem;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final OrderMapper orderMapper;
        private final OrderNumberGenerator orderNumberGenerator;
        private final ApplicationEventPublisher eventPublisher;

        @Transactional
        public OrderResponse createOrder(OrderCreateRequest request) {

                List<OrderItem> orderItems = new ArrayList<>();
                int totalCents = 0;

                for (var itemRequest : request.items()) {
                        Product product = productRepository.findById(itemRequest.productId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product with id " + itemRequest.productId() + " not found"));

                        if (!product.getActive()) {
                                throw new ProductUnavailableException("Product " + product.getSku() + " is not active");
                        }

                        if (product.getStockQty() < itemRequest.quantity()) {
                                throw new InsufficientStockException(
                                                "Product " + product.getSku() + " has only " + product.getStockQty()
                                                                + " units in stock, but "
                                                                + itemRequest.quantity() + " were requested");
                        }

                        // Snapshot of product data at the time of purchase
                        OrderItem orderItem = OrderItem.builder()
                                        .product(product)
                                        .sku(product.getSku())
                                        .productName(product.getName())
                                        .gtin14(product.getGtin14())
                                        .unitPriceCents(product.getPriceCents())
                                        .quantity(itemRequest.quantity())
                                        .lineTotalCents(product.getPriceCents() * itemRequest.quantity())
                                        .build();

                        orderItems.add(orderItem);
                        totalCents += orderItem.getLineTotalCents();
                }

                String orderNumber = orderNumberGenerator.generateOrderNumber();

                Order order = Order.builder()
                                .orderNumber(orderNumber)
                                .contactEmail(request.contactEmail())
                                .contactPhone(request.contactPhone())
                                .status(OrderStatus.NEW)
                                .totalCents(totalCents)
                                .currency(request.currency())
                                .shippingName(request.shipping().name())
                                .shippingLine1(request.shipping().line1())
                                .shippingLine2(request.shipping().line2())
                                .shippingCity(request.shipping().city())
                                .shippingState(request.shipping().state())
                                .shippingPostal(request.shipping().postal())
                                .shippingCountry(request.shipping().country())
                                .build();

                orderItems.forEach(order::addItem);

                Order savedOrder = orderRepository.save(order);

                // We publish the event (e.g. for sending an email in the future)
                eventPublisher.publishEvent(new OrderPlacedEvent(
                                savedOrder.getOrderNumber(),
                                savedOrder.getContactEmail(),
                                savedOrder.getTotalCents(),
                                savedOrder.getCurrency(),
                                savedOrder.getPlacedAt()));

                return orderMapper.toResponse(savedOrder);
        }

        @Transactional(readOnly = true)
        public Optional<OrderResponse> getOrderByOrderNumber(String orderNumber) {
                return orderRepository.findByOrderNumberWithItems(orderNumber)
                                .map(orderMapper::toResponse);
        }

}
