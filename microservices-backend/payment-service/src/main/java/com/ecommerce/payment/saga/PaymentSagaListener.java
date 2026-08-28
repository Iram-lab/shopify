package com.ecommerce.payment.saga;

import com.ecommerce.payment.dto.PaymentDtos.*;
import com.ecommerce.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaListener {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PAYMENT_REPLY_TOPIC = "saga.payment-reply";

    @KafkaListener(topics = "saga.process-payment", groupId = "payment-service-group")
    public void onProcessPaymentCommand(Map<String, Object> payload) {
        String orderNumber = (String) payload.get("orderNumber");
        String userEmail   = (String) payload.get("userEmail");
        BigDecimal amount  = new BigDecimal(payload.get("amount").toString());

        log.info("SAGA ← ProcessPaymentCommand: orderNumber={}", orderNumber);

        try {
            PaymentInitiateResponse response = paymentService.initiatePayment(
                new PaymentInitiateRequest(orderNumber, userEmail, amount)
            );

            boolean success = !"FAILED".equalsIgnoreCase(response.status());

            kafkaTemplate.send(PAYMENT_REPLY_TOPIC, orderNumber,
                new PaymentReply(
                    orderNumber, success,
                    response.paymentId(),
                    response.razorpayOrderId(),
                    response.keyId(),
                    success ? null : "Payment initiation failed"
                )
            );
            log.info("SAGA payment reply sent: orderNumber={}, success={}", orderNumber, success);

        } catch (Exception e) {
            log.error("SAGA payment FAILED: orderNumber={}, error={}", orderNumber, e.getMessage());
            kafkaTemplate.send(PAYMENT_REPLY_TOPIC, orderNumber,
                new PaymentReply(orderNumber, false, null, null, null, e.getMessage())
            );
        }
    }

    public record PaymentReply(
        String orderNumber,
        boolean success,
        String paymentId,
        String razorpayOrderId,
        String keyId,
        String reason
    ) {}
}
