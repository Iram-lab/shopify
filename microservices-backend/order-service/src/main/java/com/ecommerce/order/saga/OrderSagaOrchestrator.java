package com.ecommerce.order.saga;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.saga.SagaState.SagaStep;
import com.ecommerce.order.saga.SagaState.SagaStatus;
import com.ecommerce.order.saga.commands.SagaCommands.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SagaStateRepository sagaStateRepository;
    private final OrderRepository orderRepository;

    @Value("${saga.topics.reserve-stock}")   private String reserveStockTopic;
    @Value("${saga.topics.stock-reply}")     private String stockReplyTopic;
    @Value("${saga.topics.process-payment}") private String processPaymentTopic;
    @Value("${saga.topics.payment-reply}")   private String paymentReplyTopic;
    @Value("${saga.topics.send-notification}") private String sendNotificationTopic;

    // ── STEP 1: Called by OrderService when order is saved as PENDING ──
    @Transactional
    public void startSaga(Order order) {
        log.info("SAGA START: orderNumber={}", order.getOrderNumber());

        // Persist saga state
        SagaState state = SagaState.builder()
            .orderNumber(order.getOrderNumber())
            .userEmail(order.getUserEmail())
            .currentStep(SagaStep.RESERVE_STOCK)
            .status(SagaStatus.STARTED)
            .build();
        sagaStateRepository.save(state);

        // Update order status
        order.setStatus(OrderStatus.STOCK_RESERVING);
        orderRepository.save(order);

        // Build items list from order
        List<OrderItem> items = order.getItems().stream()
            .map(i -> new OrderItem(i.getProductId(), i.getQuantity()))
            .toList();

        // Send command to inventory-service
        ReserveStockCommand cmd = new ReserveStockCommand(
            order.getOrderNumber(), order.getUserEmail(), items
        );
        kafkaTemplate.send(reserveStockTopic, order.getOrderNumber(), cmd);
        log.info("SAGA → sent ReserveStockCommand: orderNumber={}", order.getOrderNumber());
    }

    // ── STEP 2: Receive stock reply from inventory-service ──
    @Transactional
    @KafkaListener(topics = "${saga.topics.stock-reply}", groupId = "order-service-group")
    public void onStockReply(StockReply reply) {
        log.info("SAGA ← StockReply: orderNumber={}, success={}", reply.orderNumber(), reply.success());

        SagaState state = sagaStateRepository.findById(reply.orderNumber())
            .orElseThrow(() -> new IllegalStateException("Saga not found: " + reply.orderNumber()));
        Order order = orderRepository.findByOrderNumber(reply.orderNumber())
            .orElseThrow(() -> new IllegalStateException("Order not found: " + reply.orderNumber()));

        if (reply.success()) {
            // Stock reserved — move to payment step
            state.setCurrentStep(SagaStep.PROCESS_PAYMENT);
            order.setStatus(OrderStatus.PAYMENT_PROCESSING);
            sagaStateRepository.save(state);
            orderRepository.save(order);

            ProcessPaymentCommand cmd = new ProcessPaymentCommand(
                order.getOrderNumber(), order.getUserEmail(), order.getTotalAmount()
            );
            kafkaTemplate.send(processPaymentTopic, order.getOrderNumber(), cmd);
            log.info("SAGA → sent ProcessPaymentCommand: orderNumber={}", order.getOrderNumber());

        } else {
            // Stock failed — saga ends, no compensation needed (nothing was done yet)
            log.warn("SAGA stock reservation FAILED: orderNumber={}, reason={}", reply.orderNumber(), reply.reason());
            state.setStatus(SagaStatus.FAILED);
            state.setFailureReason(reply.reason());
            order.setStatus(OrderStatus.CANCELLED);
            sagaStateRepository.save(state);
            orderRepository.save(order);

            // Notify user of cancellation
            sendNotification(order.getOrderNumber(), order.getUserEmail(), "CANCELLED", order.getTotalAmount());
        }
    }

    // ── STEP 3: Receive payment reply from payment-service ──
    @Transactional
    @KafkaListener(topics = "${saga.topics.payment-reply}", groupId = "order-service-group")
    public void onPaymentReply(PaymentReply reply) {
        log.info("SAGA ← PaymentReply: orderNumber={}, success={}", reply.orderNumber(), reply.success());

        SagaState state = sagaStateRepository.findById(reply.orderNumber())
            .orElseThrow(() -> new IllegalStateException("Saga not found: " + reply.orderNumber()));
        Order order = orderRepository.findByOrderNumber(reply.orderNumber())
            .orElseThrow(() -> new IllegalStateException("Order not found: " + reply.orderNumber()));

        if (reply.success()) {
            // Payment succeeded — saga complete
            state.setCurrentStep(SagaStep.SEND_NOTIFICATION);
            state.setStatus(SagaStatus.COMPLETED);
            order.setStatus(OrderStatus.CONFIRMED);
            order.setPaymentId(reply.paymentId());
            order.setRazorpayOrderId(reply.razorpayOrderId());
            order.setRazorpayKeyId(reply.keyId());
            sagaStateRepository.save(state);
            orderRepository.save(order);

            log.info("SAGA COMPLETE ✅: orderNumber={}", order.getOrderNumber());
            sendNotification(order.getOrderNumber(), order.getUserEmail(), "CONFIRMED", order.getTotalAmount());

        } else {
            // Payment failed — compensate: release stock
            log.warn("SAGA payment FAILED: orderNumber={}, reason={}", reply.orderNumber(), reply.reason());
            state.setCurrentStep(SagaStep.COMPENSATE_STOCK);
            state.setStatus(SagaStatus.COMPENSATING);
            state.setFailureReason(reply.reason());
            order.setStatus(OrderStatus.CANCELLING);
            sagaStateRepository.save(state);
            orderRepository.save(order);

            // Send compensation command to inventory-service
            List<OrderItem> items = order.getItems().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity()))
                .toList();
            ReleaseStockCommand cmd = new ReleaseStockCommand(order.getOrderNumber(), items);
            kafkaTemplate.send(reserveStockTopic, order.getOrderNumber(), cmd);
            log.info("SAGA → sent ReleaseStockCommand (compensation): orderNumber={}", order.getOrderNumber());
        }
    }

    // ── Called after compensation stock release is confirmed ──
    @Transactional
    public void onCompensationComplete(String orderNumber) {
        SagaState state = sagaStateRepository.findById(orderNumber).orElse(null);
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if (state != null) { state.setStatus(SagaStatus.FAILED); sagaStateRepository.save(state); }
        if (order != null) { order.setStatus(OrderStatus.CANCELLED); orderRepository.save(order); }
        log.info("SAGA COMPENSATED: orderNumber={}", orderNumber);
        if (order != null) sendNotification(orderNumber, order.getUserEmail(), "CANCELLED", order.getTotalAmount());
    }

    private void sendNotification(String orderNumber, String userEmail, String status, java.math.BigDecimal amount) {
        SendNotificationCommand cmd = new SendNotificationCommand(orderNumber, userEmail, status, amount);
        kafkaTemplate.send(sendNotificationTopic, orderNumber, cmd);
        log.info("SAGA → sent SendNotificationCommand: orderNumber={}, status={}", orderNumber, status);
    }
}
