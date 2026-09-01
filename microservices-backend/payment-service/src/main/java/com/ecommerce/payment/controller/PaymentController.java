package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentDtos.*;
import com.ecommerce.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and status")
public class PaymentController {

    private final PaymentService paymentService;

    // Called by Order Service via Feign — creates Razorpay order
    @PostMapping("/initiate")
    @Operation(summary = "Initiate Razorpay payment for an order (called by Order Service)")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(request));
    }

    // Called by Frontend after user completes payment on Razorpay popup
    @PostMapping("/verify")
    @Operation(summary = "Verify Razorpay payment signature (called by Frontend)")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request) {
                log.info("product is added");
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details by payment ID")
    public ResponseEntity<PaymentResponse> findByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.findByPaymentId(paymentId));
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(summary = "Get payment by order number")
    public ResponseEntity<PaymentResponse> findByOrderNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(paymentService.findByOrderNumber(orderNumber));
    }

    @GetMapping("/my-payments")
    @Operation(summary = "Get all payments for current user")
    public ResponseEntity<List<PaymentResponse>> findMyPayments(
            @RequestHeader("X-Auth-User") String userEmail) {
        return ResponseEntity.ok(paymentService.findByUserEmail(userEmail));
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refund a payment (ADMIN only)")
    public ResponseEntity<PaymentResponse> refund(@Valid @RequestBody RefundRequest request) {
        log.info("Refunded money");
        return ResponseEntity.ok(paymentService.refund(request));
    }
}
