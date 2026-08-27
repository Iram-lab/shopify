package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderDtos.*;
import com.ecommerce.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and tracking")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Place a new order from cart")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestHeader("X-Auth-User") String userEmail,
            @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.placeOrder(userEmail, request));
    }

    @GetMapping("/my-orders")
    @Operation(summary = "Get current user's order history")
    public ResponseEntity<PagedResponse<OrderResponse>> getMyOrders(
            @RequestHeader("X-Auth-User") String userEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.findMyOrders(userEmail, page, size));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Get order details by order number")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("X-Auth-User") String userEmail,
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.findByOrderNumber(orderNumber, userEmail));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders (ADMIN only)")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.findAllOrders(page, size));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (ADMIN only)")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request));
    }

    @PostMapping("/{orderNumber}/confirm")
    @Operation(summary = "Confirm order after successful payment verification")
    public ResponseEntity<OrderResponse> confirmOrder(
            @RequestHeader("X-Auth-User") String userEmail,
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(orderService.confirmOrder(orderNumber, userEmail));
    }
}
