package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartDtos.*;
import com.ecommerce.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader("X-Auth-User") String userEmail,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItem(userEmail, request));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Update item quantity in cart")
    public ResponseEntity<CartResponse> updateItem(
            @RequestHeader("X-Auth-User") String userEmail,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userEmail, productId, request));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("X-Auth-User") String userEmail,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userEmail, productId));
    }

    @DeleteMapping
    @Operation(summary = "Clear entire cart")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-Auth-User") String userEmail) {
        cartService.clearCart(userEmail);
        return ResponseEntity.noContent().build();
    }

    // Internal endpoint called by Order Service via Feign
    @GetMapping("/summary")
    @Operation(summary = "Get cart summary (used internally by Order Service)")
    public ResponseEntity<CartSummary> getCartSummary(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(cartService.getCartSummary(userEmail));
    }
}
