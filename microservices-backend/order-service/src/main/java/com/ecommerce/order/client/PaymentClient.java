package com.ecommerce.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "payment-service", fallback = PaymentClient.PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentResponse initiatePayment(@RequestBody PaymentRequest request);

    record PaymentRequest(
        String orderNumber,
        String userEmail,
        BigDecimal amount
    ) {}

    record PaymentResponse(
        String paymentId,
        String razorpayOrderId,
        String currency,
        java.math.BigDecimal amount,
        String keyId,
        String status,
        String message
    ) {}

    @Component
    @Slf4j
    class PaymentClientFallback implements PaymentClient {

        @Override
        public PaymentResponse initiatePayment(PaymentRequest request) {
            log.error("Payment service unavailable. Circuit breaker OPEN for order={}",
                request.orderNumber());
            return new PaymentResponse(null, null, null, null, null, "FAILED",
                "Payment service is currently unavailable. Please try again.");
        }
    }
}
