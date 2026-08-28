package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.CartClient.CartItemResponse;
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

    /**
     * Saga-based order placement:
     * 1. Fetch cart
     * 2. Save order as PENDING
     * 3. Start saga — orchestrator handles inventory + payment async via Kafka
     */
    @Transactional
    public OrderResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        CartSummary cart = cartClient.getCartSummary(userEmail);
        if (cart.items() == null || cart.items().isEmpty()) {
            throw new OrderException("Cannot place order: cart is empty.");
        }

        Order order = buildOrder(userEmail, cart, request.shippingAddress());
        order = orderRepository.save(order);
        log.info("Order created: orderNumber={}, status=PENDING", order.getOrderNumber());

        // Start the saga — all further steps are async via Kafka
        sagaOrchestrator.startSaga(order);

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

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderMapper mapper;

    /**
     * Full order placement workflow:
     * 1. Fetch cart items from Cart Service
     * 2. Validate stock via Inventory Service (bulk check)
     * 3. Save order as PENDING
     * 4. Deduct stock via Inventory Service
     * 5. Initiate payment via Payment Service
     * 6. Update order status based on payment result
     * 7. Clear cart
     */
    @Transactional
    @CircuitBreaker(name = "order-placement", fallbackMethod = "placeOrderFallback")
    public OrderResponse placeOrder(String userEmail, PlaceOrderRequest request) {
        // Step 1: Fetch cart
        CartSummary cart = cartClient.getCartSummary(userEmail);
        if (cart.items() == null || cart.items().isEmpty()) {
            throw new OrderException("Cannot place order: cart is empty.");
        }

        // Step 2: Bulk stock validation
        List<StockCheckRequest> stockChecks = cart.items().stream()
            .map(item -> new StockCheckRequest(item.productId(), item.quantity()))
            .toList();

        List<StockCheckResponse> stockResults = inventoryClient
            .checkBulkStock(new BulkStockCheckRequest(stockChecks));

        List<StockCheckResponse> outOfStock = stockResults.stream()
            .filter(r -> !r.inStock())
            .toList();

        if (!outOfStock.isEmpty()) {
            String productIds = outOfStock.stream()
                .map(r -> String.valueOf(r.productId()))
                .reduce((a, b) -> a + ", " + b).orElse("");
            throw new OrderException("Out of stock for product(s): " + productIds);
        }

        // Step 3: Save order as PENDING
        Order order = buildOrder(userEmail, cart, request.shippingAddress());
        order = orderRepository.save(order);
        log.info("Order created: orderNumber={}, status=PENDING", order.getOrderNumber());

        // Step 4: Deduct stock
        List<StockDeductRequest> deductions = cart.items().stream()
            .map(item -> new StockDeductRequest(item.productId(), item.quantity()))
            .toList();
        inventoryClient.deductStock(new BulkStockDeductRequest(deductions));
        log.info("Stock deducted for order: {}", order.getOrderNumber());

        // Step 5: Initiate payment
        PaymentResponse payment = paymentClient.initiatePayment(
            new PaymentRequest(order.getOrderNumber(), userEmail, order.getTotalAmount())
        );

        // Step 6: Store Razorpay details — order stays PENDING until frontend verifies payment
        if (payment.razorpayOrderId() != null && !"FAILED".equals(payment.status())) {
            order.setRazorpayOrderId(payment.razorpayOrderId());
            order.setRazorpayKeyId(payment.keyId());
            order.setPaymentId(payment.paymentId());
            log.info("Razorpay order created: orderNumber={}, razorpayOrderId={}, keyId={}",
                order.getOrderNumber(), payment.razorpayOrderId(), payment.keyId());
        } else {
            restockOnFailure(cart.items());
            order.setStatus(OrderStatus.CANCELLED);
            log.warn("Payment initiation failed for order: {}. status={}, razorpayOrderId={}",
                order.getOrderNumber(), payment.status(), payment.razorpayOrderId());
        }

        order = orderRepository.save(order);
        log.info("Order saved: orderNumber={}, razorpayOrderId={}, razorpayKeyId={}",
            order.getOrderNumber(), order.getRazorpayOrderId(), order.getRazorpayKeyId());

        return mapper.toDto(order);
    }

    public OrderResponse placeOrderFallback(String userEmail, PlaceOrderRequest request, Throwable t) {
        log.error("Order placement circuit breaker OPEN for user={}. Cause: {}", userEmail, t.getMessage());
        throw new OrderException("Order service temporarily unavailable. Please try again.");
    }

    public OrderResponse findByOrderNumber(String orderNumber, String userEmail) {
        Order order = orderRepository.findByOrderNumberWithItems(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        if (!order.getUserEmail().equals(userEmail)) {
            throw new OrderException("Access denied to order: " + orderNumber);
        }
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

        // Restock if cancelling a confirmed order
        if (request.status() == OrderStatus.CANCELLED
                && order.getStatus() == OrderStatus.CONFIRMED) {
            order.getItems().forEach(item ->
                inventoryClient.restockProduct(
                    new RestockRequest(item.getProductId(), item.getQuantity()))
            );
            log.info("Restocked items for cancelled order: {}", order.getOrderNumber());
        }

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

    private void restockOnFailure(List<CartItemResponse> items) {
        items.forEach(item -> {
            try {
                inventoryClient.restockProduct(
                    new RestockRequest(item.productId(), item.quantity()));
            } catch (Exception e) {
                log.error("Failed to restock productId={} after payment failure", item.productId());
            }
        });
    }

    private PagedResponse<OrderResponse> toPagedResponse(Page<Order> page) {
        return new PagedResponse<>(
            page.getContent().stream().map(mapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
