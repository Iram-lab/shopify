package com.ecommerce.notification.controller;

import com.ecommerce.notification.dto.NotificationDtos.*;
import com.ecommerce.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Email notification management")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/order-confirmation")
    @Operation(summary = "Send order confirmation email (called by Order Service)")
    public ResponseEntity<Void> sendOrderConfirmation(
            @Valid @RequestBody OrderConfirmationRequest request) {
        notificationService.sendOrderConfirmation(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order-status")
    @Operation(summary = "Send order status update email (called by Order Service)")
    public ResponseEntity<Void> sendOrderStatusUpdate(
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        notificationService.sendOrderStatusUpdate(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment")
    @Operation(summary = "Send payment notification email (called by Payment Service)")
    public ResponseEntity<Void> sendPaymentNotification(
            @Valid @RequestBody PaymentNotificationRequest request) {
        notificationService.sendPaymentNotification(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/welcome")
    @Operation(summary = "Send welcome email on registration (called by Auth Service)")
    public ResponseEntity<Void> sendWelcomeEmail(
            @Valid @RequestBody WelcomeRequest request) {
        notificationService.sendWelcomeEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all notifications for current user")
    public ResponseEntity<List<NotificationResponse>> findMyNotifications(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(notificationService.findByEmail(userEmail));
    }
}
