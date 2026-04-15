package com.onlineshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.onlineshop.dto.order.OrderItemResponse;
import com.onlineshop.dto.order.OrderResponse;
import com.onlineshop.entity.Order;
import com.onlineshop.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}