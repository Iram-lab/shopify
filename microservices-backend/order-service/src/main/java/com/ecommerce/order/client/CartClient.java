package com.ecommerce.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@FeignClient(name = "cart-service", fallback = CartClient.CartClientFallback.class)
public interface CartClient {

    @GetMapping("/api/cart/summary")
    CartSummary getCartSummary(@RequestHeader("X-Auth-User") String userEmail);

    @DeleteMapping("/api/cart")
    void clearCart(@RequestHeader("X-Auth-User") String userEmail);

    record CartItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        String imageUrl,
        Integer quantity,
        BigDecimal subtotal
    ) {}

    record CartSummary(
        String userEmail,
        List<CartItemResponse> items,
        BigDecimal totalPrice
    ) {}

    @Component
    @Slf4j
    class CartClientFallback implements CartClient {

        @Override
        public CartSummary getCartSummary(String userEmail) {
            log.warn("Cart service unavailable. Fallback for user={}", userEmail);
            return new CartSummary(userEmail, Collections.emptyList(), BigDecimal.ZERO);
        }

        @Override
        public void clearCart(String userEmail) {
            log.warn("Cart service unavailable. Could not clear cart for user={}", userEmail);
        }
    }
}
