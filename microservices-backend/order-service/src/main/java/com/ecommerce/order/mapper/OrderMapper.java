package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderDtos.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderItemResponse toDto(OrderItem item);

    @org.mapstruct.Mapping(target = "razorpayOrderId", source = "razorpayOrderId")
    @org.mapstruct.Mapping(target = "razorpayKeyId",   source = "razorpayKeyId")
    OrderResponse toDto(Order order);
}
