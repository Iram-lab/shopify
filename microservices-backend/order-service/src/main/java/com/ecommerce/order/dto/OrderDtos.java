package com.ecommerce.order.dto;

import com.ecommerce.order.entity.OrderStatus;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDtos {

    public record PlaceOrderRequest(
        @NotBlank String shippingAddress
    ) {}

    public record UpdateOrderStatusRequest(
        OrderStatus status
    ) {}

    public record OrderItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
    ) {}

    public record OrderResponse(
        Long id,
        String orderNumber,
        String userEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        String shippingAddress,
        String paymentId,
        String razorpayOrderId,
        String razorpayKeyId,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
    ) {}
}
