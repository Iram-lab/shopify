package com.ecommerce.notification.dto;

import com.ecommerce.notification.entity.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationDtos {

    public record OrderConfirmationRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String orderNumber,
        @NotNull BigDecimal totalAmount,
        @NotBlank String shippingAddress,
        List<OrderItemDto> items
    ) {}

    public record OrderStatusUpdateRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String orderNumber,
        @NotBlank String newStatus
    ) {}

    public record PaymentNotificationRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String orderNumber,
        @NotBlank String paymentId,
        @NotNull BigDecimal amount,
        @NotBlank String status
    ) {}

    public record WelcomeRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName
    ) {}

    public record OrderItemDto(
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}

    public record NotificationResponse(
        Long id,
        String recipientEmail,
        String subject,
        NotificationType type,
        String referenceId,
        boolean sent,
        String failureReason,
        LocalDateTime createdAt
    ) {}
}
