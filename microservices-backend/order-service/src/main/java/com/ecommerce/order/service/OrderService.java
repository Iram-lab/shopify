package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.CartClient.CartSummary;
import com.ecommerce.order.dto.OrderDtos.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.OrderException;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.saga.OrderSagaOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final OrderMapper mapper;
    private final OrderSagaOrchestrator sagaOrchestrator;

    @Transactional
    public OrderResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        CartSummary cart = cartClient.getCartSummary(userEmail);
        if (cart.items() == null || cart.items().isEmpty()) {
            throw new OrderException("Cannot place order: cart is empty.");
        }
        Order order = buildOrder(userEmail, cart, request.shippingAddress());
        order = orderRepository.save(order);
        log.info("Order created: orderNumber={}, status=PENDING", order.getOrderNumber());
        sagaOrchestrator.startSaga(order);
        return mapper.toDto(order);
    }

    @Transactional
    public OrderResponse confirmOrder(String orderNumber, String userEmail) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        if (!order.getUserEmail().equals(userEmail)) {
            throw new OrderException("Access denied to order: " + orderNumber);
        }
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);
        cartClient.clearCart(userEmail);
        log.info("Order CONFIRMED after payment: {}", orderNumber);
        return mapper.toDto(order);
    }

    public OrderResponse findByOrderNumber(String orderNumber, String userEmail) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        if (!order.getUserEmail().equals(userEmail)) {
            throw new OrderException("Access denied to order: " + orderNumber);
        }
        return mapper.toDto(order);
    }

    public PagedResponse<OrderResponse> findMyOrders(String userEmail, int page, int size) {
        Page<Order> orders = orderRepository.findByUserEmailOrderByCreatedAtDesc(
            userEmail, PageRequest.of(page, size));
        return toPagedResponse(orders);
    }

    public PagedResponse<OrderResponse> findAllOrders(int page, int size) {
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page, size));
        return toPagedResponse(orders);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdWithItems(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        order.setStatus(request.status());
        return mapper.toDto(orderRepository.save(order));
    }

    private Order buildOrder(String userEmail, CartSummary cart, String shippingAddress) {
        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .userEmail(userEmail)
            .status(OrderStatus.PENDING)
            .totalAmount(cart.totalPrice())
            .shippingAddress(shippingAddress)
            .build();
        List<OrderItem> items = cart.items().stream()
            .map(cartItem -> OrderItem.builder()
                .order(order)
                .productId(cartItem.productId())
                .productName(cartItem.productName())
                .unitPrice(cartItem.unitPrice())
                .quantity(cartItem.quantity())
                .subtotal(cartItem.subtotal())
                .build())
            .toList();
        order.getItems().addAll(items);
        return order;
    }

    private PagedResponse<OrderResponse> toPagedResponse(Page<Order> page) {
        return new PagedResponse<>(
            page.getContent().stream().map(mapper::toDto).toList(),
            page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.isLast()
        );
    }
}
