package com.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CartDtos {

    public record AddToCartRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
    ) {}

    public record UpdateCartItemRequest(
        @NotNull @Min(1) Integer quantity
    ) {}

    public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        String imageUrl,
        Integer quantity,
        BigDecimal subtotal
    ) {}

    public record CartResponse(
        Long id,
        String userEmail,
        List<CartItemResponse> items,
        int totalItems,
        BigDecimal totalPrice,
        LocalDateTime updatedAt
    ) {}

    // Used internally by Order Service via Feign to fetch cart contents
    public record CartSummary(
        String userEmail,
        List<CartItemResponse> items,
        BigDecimal totalPrice
    ) {}
}
