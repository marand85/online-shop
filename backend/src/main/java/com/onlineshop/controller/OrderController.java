package com.onlineshop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onlineshop.dto.order.OrderCreateRequest;
import com.onlineshop.dto.order.OrderResponse;
import com.onlineshop.exception.ResourceNotFoundException;
import com.onlineshop.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Orders", description = "Order management endpoints")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

        @Operation(summary = "Create new order", description = "Creates a new guest order with shipping details")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Order created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "409", description = "Insufficient stock"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping
        public ResponseEntity<OrderResponse> createOrder(
                        @Valid @RequestBody OrderCreateRequest request) {

                OrderResponse response = orderService.createOrder(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Get order by order number", description = "Returns order details by its public order number")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Order found"),
                        @ApiResponse(responseCode = "404", description = "Order not found"),
                        @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/{orderNumber}")
        public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderNumber) {
                return orderService.getOrderByOrderNumber(orderNumber)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Order with number " + orderNumber + " not found"));
        }
}
