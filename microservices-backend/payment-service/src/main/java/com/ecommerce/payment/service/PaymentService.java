package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentDtos.*;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import com.ecommerce.payment.exception.PaymentException;
import com.ecommerce.payment.exception.ResourceNotFoundException;
import com.ecommerce.payment.mapper.PaymentMapper;
import com.ecommerce.payment.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper mapper;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentMapper mapper,
                          @Value("${razorpay.key-id}") String keyId,
                          @Value("${razorpay.key-secret}") String keySecret) throws RazorpayException {
        this.paymentRepository = paymentRepository;
        this.mapper = mapper;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    /**
     * Step 1: Called by Order Service.
     * Creates a Razorpay order and returns order_id + key_id to frontend.
     */
    @Transactional
    public PaymentInitiateResponse initiatePayment(PaymentInitiateRequest request) {
        // Idempotency check
        if (paymentRepository.existsByOrderNumber(request.orderNumber())) {
            Payment existing = paymentRepository.findByOrderNumber(request.orderNumber()).orElseThrow();
            log.warn("Duplicate payment attempt for order: {}", request.orderNumber());
            return new PaymentInitiateResponse(
                existing.getPaymentId(),
                existing.getRazorpayOrderId(),
                "INR",
                existing.getAmount(),
                keyId,
                existing.getStatus().name(),
                "Payment already initiated"
            );
        }

        try {
            // Amount in paise (Razorpay requires smallest currency unit)
            int amountInPaise = request.amount()
                .multiply(BigDecimal.valueOf(100))
                .intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", request.orderNumber());
            orderRequest.put("payment_capture", 1);
            orderRequest.put("notes", new JSONObject()
                .put("order_number", request.orderNumber())
                .put("user_email", request.userEmail())
            );

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            String internalPaymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Save as PENDING — will be updated after frontend verifies
            Payment payment = Payment.builder()
                .paymentId(internalPaymentId)
                .orderNumber(request.orderNumber())
                .userEmail(request.userEmail())
                .amount(request.amount())
                .status(PaymentStatus.PENDING)
                .razorpayOrderId(razorpayOrderId)
                .build();

            paymentRepository.save(payment);
            log.info("Razorpay order created: razorpayOrderId={}, order={}", razorpayOrderId, request.orderNumber());

            return new PaymentInitiateResponse(
                internalPaymentId,
                razorpayOrderId,
                "INR",
                request.amount(),
                keyId,
                "PENDING",
                "Razorpay order created. Complete payment on frontend."
            );

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed for order={}: {}", request.orderNumber(), e.getMessage());
            throw new PaymentException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    /**
     * Step 2: Called by frontend after user completes payment.
     * Verifies Razorpay signature and marks payment SUCCESS or FAILED.
     */
    @Transactional
    public PaymentVerifyResponse verifyPayment(PaymentVerifyRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found for order: " + request.orderNumber()));

        try {
            // Verify HMAC SHA256 signature
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id",   request.razorpayOrderId());
            attributes.put("razorpay_payment_id",  request.razorpayPaymentId());
            attributes.put("razorpay_signature",   request.razorpaySignature());

            boolean isValid = Utils.verifyPaymentSignature(attributes, keySecret);

            if (isValid) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setRazorpayPaymentId(request.razorpayPaymentId());
                payment.setTransactionRef(request.razorpayPaymentId());
                paymentRepository.save(payment);
                log.info("Payment verified SUCCESS: order={}, razorpayPaymentId={}",
                    request.orderNumber(), request.razorpayPaymentId());
                return new PaymentVerifyResponse(payment.getPaymentId(), "SUCCESS",
                    "Payment verified successfully");
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Invalid payment signature");
                paymentRepository.save(payment);
                log.warn("Payment signature INVALID for order={}", request.orderNumber());
                return new PaymentVerifyResponse(payment.getPaymentId(), "FAILED",
                    "Payment verification failed: invalid signature");
            }

        } catch (RazorpayException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Signature verification error: " + e.getMessage());
            paymentRepository.save(payment);
            log.error("Signature verification error for order={}: {}", request.orderNumber(), e.getMessage());
            return new PaymentVerifyResponse(payment.getPaymentId(), "FAILED",
                "Payment verification error");
        }
    }

    public PaymentResponse findByPaymentId(String paymentId) {
        return mapper.toDto(paymentRepository.findByPaymentId(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId)));
    }

    public PaymentResponse findByOrderNumber(String orderNumber) {
        return mapper.toDto(paymentRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found for order: " + orderNumber)));
    }

    public List<PaymentResponse> findByUserEmail(String userEmail) {
        return paymentRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
            .stream().map(mapper::toDto).toList();
    }

    @Transactional
    public PaymentResponse refund(RefundRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Payment not found for order: " + request.orderNumber()));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot refund payment with status: " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason(request.reason());
        log.info("Payment REFUNDED: paymentId={}, order={}", payment.getPaymentId(), request.orderNumber());
        return mapper.toDto(paymentRepository.save(payment));
    }
}
