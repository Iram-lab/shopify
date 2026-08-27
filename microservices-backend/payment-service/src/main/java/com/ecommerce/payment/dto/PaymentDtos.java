package com.ecommerce.payment.dto;

import com.ecommerce.payment.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDtos {

    // Called by Order Service to create a Razorpay order
    public record PaymentInitiateRequest(
        @NotBlank String orderNumber,
        @NotBlank String userEmail,
        @NotNull @DecimalMin("0.01") BigDecimal amount
    ) {}

    // Returned to Order Service / Frontend with Razorpay order details
    public record PaymentInitiateResponse(
        String paymentId,        // internal payment ID
        String razorpayOrderId,  // Razorpay order_id (used by frontend checkout)
        String currency,
        BigDecimal amount,
        String keyId,            // Razorpay key_id (needed by frontend)
        String status,
        String message
    ) {}

    // Sent by frontend after user completes payment on Razorpay popup
    public record PaymentVerifyRequest(
        @NotBlank String orderNumber,
        @NotBlank String razorpayOrderId,
        @NotBlank String razorpayPaymentId,
        @NotBlank String razorpaySignature
    ) {}

    // Response after verification
    public record PaymentVerifyResponse(
        String paymentId,
        String status,
        String message
    ) {}

    public record PaymentResponse(
        Long id,
        String paymentId,
        String orderNumber,
        String userEmail,
        BigDecimal amount,
        PaymentStatus status,
        String transactionRef,
        String razorpayOrderId,
        String failureReason,
        LocalDateTime createdAt
    ) {}

    public record RefundRequest(
        @NotBlank String orderNumber,
        String reason
    ) {}
}
