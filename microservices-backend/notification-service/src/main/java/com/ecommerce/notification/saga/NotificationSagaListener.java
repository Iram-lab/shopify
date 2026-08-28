package com.ecommerce.notification.saga;

import com.ecommerce.notification.dto.NotificationDtos.*;
import com.ecommerce.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSagaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "saga.send-notification", groupId = "notification-service-group")
    public void onSendNotificationCommand(Map<String, Object> payload) {
        String orderNumber = (String) payload.get("orderNumber");
        String userEmail   = (String) payload.get("userEmail");
        String status      = (String) payload.get("status");
        BigDecimal amount  = new BigDecimal(payload.get("amount").toString());

        log.info("SAGA ← SendNotificationCommand: orderNumber={}, status={}", orderNumber, status);

        if ("CONFIRMED".equals(status)) {
            notificationService.sendOrderConfirmation(new OrderConfirmationRequest(
                userEmail, userEmail, orderNumber, amount, "N/A", List.of()
            ));
        } else {
            notificationService.sendOrderStatusUpdate(new OrderStatusUpdateRequest(
                userEmail, userEmail, orderNumber, status
            ));
        }
    }
}
