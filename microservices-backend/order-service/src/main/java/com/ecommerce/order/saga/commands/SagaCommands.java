package com.ecommerce.order.saga.commands;

import java.math.BigDecimal;
import java.util.List;

// ── Commands sent BY orchestrator TO services ──────────────────────────

public class SagaCommands {

    // Sent to inventory-service: reserve stock for this order
    public record ReserveStockCommand(
        String orderNumber,
        String userEmail,
        List<OrderItem> items
    ) {}

    // Sent to inventory-service: release stock (compensation)
    public record ReleaseStockCommand(
        String orderNumber,
        List<OrderItem> items
    ) {}

    // Sent to payment-service: process payment
    public record ProcessPaymentCommand(
        String orderNumber,
        String userEmail,
        BigDecimal amount
    ) {}

    // Sent to notification-service: send email
    public record SendNotificationCommand(
        String orderNumber,
        String userEmail,
        String status,          // CONFIRMED or CANCELLED
        BigDecimal amount
    ) {}

    // ── Replies sent BY services BACK to orchestrator ──────────────────

    public record StockReply(
        String orderNumber,
        boolean success,
        String reason           // null on success, error message on failure
    ) {}

    public record PaymentReply(
        String orderNumber,
        boolean success,
        String paymentId,
        String razorpayOrderId,
        String keyId,
        String reason
    ) {}

    // Shared item record used in commands
    public record OrderItem(
        Long productId,
        int quantity
    ) {}
}
