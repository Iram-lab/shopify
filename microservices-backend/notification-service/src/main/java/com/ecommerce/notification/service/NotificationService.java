package com.ecommerce.notification.service;

import com.ecommerce.notification.dto.NotificationDtos.*;
import com.ecommerce.notification.entity.Notification;
import com.ecommerce.notification.entity.NotificationType;
import com.ecommerce.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public void sendOrderConfirmation(OrderConfirmationRequest request) {
        String subject = "Order Confirmed - " + request.orderNumber();
        Notification notification = buildNotification(
            request.email(), subject,
            NotificationType.ORDER_CONFIRMATION,
            request.orderNumber()
        );

        try {
            emailService.sendHtmlEmail(
                request.email(), subject,
                "order-confirmation",
                Map.of(
                    "firstName", request.firstName(),
                    "orderNumber", request.orderNumber(),
                    "totalAmount", request.totalAmount(),
                    "shippingAddress", request.shippingAddress(),
                    "items", request.items()
                )
            );
            notification.setSent(true);
        } catch (Exception e) {
            notification.setFailureReason(e.getMessage());
            log.error("Order confirmation email failed for order: {}", request.orderNumber());
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void sendOrderStatusUpdate(OrderStatusUpdateRequest request) {
        String subject = "Order Update - " + request.orderNumber() + " is now " + request.newStatus();
        Notification notification = buildNotification(
            request.email(), subject,
            NotificationType.ORDER_STATUS_UPDATE,
            request.orderNumber()
        );

        try {
            emailService.sendHtmlEmail(
                request.email(), subject,
                "order-status",
                Map.of(
                    "firstName", request.firstName(),
                    "orderNumber", request.orderNumber(),
                    "newStatus", request.newStatus()
                )
            );
            notification.setSent(true);
        } catch (Exception e) {
            notification.setFailureReason(e.getMessage());
            log.error("Order status email failed for order: {}", request.orderNumber());
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void sendPaymentNotification(PaymentNotificationRequest request) {
        boolean isSuccess = "SUCCESS".equalsIgnoreCase(request.status());
        String subject = isSuccess
            ? "Payment Successful - " + request.orderNumber()
            : "Payment Failed - " + request.orderNumber();

        NotificationType type = isSuccess
            ? NotificationType.PAYMENT_SUCCESS
            : NotificationType.PAYMENT_FAILED;

        Notification notification = buildNotification(
            request.email(), subject, type, request.paymentId()
        );

        try {
            emailService.sendHtmlEmail(
                request.email(), subject,
                "payment-status",
                Map.of(
                    "firstName", request.firstName(),
                    "orderNumber", request.orderNumber(),
                    "paymentId", request.paymentId(),
                    "amount", request.amount(),
                    "status", request.status(),
                    "isSuccess", isSuccess
                )
            );
            notification.setSent(true);
        } catch (Exception e) {
            notification.setFailureReason(e.getMessage());
            log.error("Payment notification email failed for payment: {}", request.paymentId());
        }

        notificationRepository.save(notification);
    }

    @Transactional
    public void sendWelcomeEmail(WelcomeRequest request) {
        String subject = "Welcome to ECommerce!";
        Notification notification = buildNotification(
            request.email(), subject,
            NotificationType.WELCOME, null
        );

        try {
            emailService.sendHtmlEmail(
                request.email(), subject,
                "welcome",
                Map.of("firstName", request.firstName())
            );
            notification.setSent(true);
        } catch (Exception e) {
            notification.setFailureReason(e.getMessage());
            log.error("Welcome email failed for: {}", request.email());
        }

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> findByEmail(String email) {
        return notificationRepository
            .findByRecipientEmailOrderByCreatedAtDesc(email)
            .stream()
            .map(this::toDto)
            .toList();
    }

    private Notification buildNotification(String email, String subject,
                                            NotificationType type, String referenceId) {
        return Notification.builder()
            .recipientEmail(email)
            .subject(subject)
            .type(type)
            .referenceId(referenceId)
            .sent(false)
            .build();
    }

    private NotificationResponse toDto(Notification n) {
        return new NotificationResponse(
            n.getId(), n.getRecipientEmail(), n.getSubject(),
            n.getType(), n.getReferenceId(), n.isSent(),
            n.getFailureReason(), n.getCreatedAt()
        );
    }
}
