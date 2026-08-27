package com.ecommerce.payment.mapper;

import com.ecommerce.payment.dto.PaymentDtos;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T16:37:35+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentDtos.PaymentResponse toDto(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        Long id = null;
        String paymentId = null;
        String orderNumber = null;
        String userEmail = null;
        BigDecimal amount = null;
        PaymentStatus status = null;
        String transactionRef = null;
        String razorpayOrderId = null;
        String failureReason = null;
        LocalDateTime createdAt = null;

        id = payment.getId();
        paymentId = payment.getPaymentId();
        orderNumber = payment.getOrderNumber();
        userEmail = payment.getUserEmail();
        amount = payment.getAmount();
        status = payment.getStatus();
        transactionRef = payment.getTransactionRef();
        razorpayOrderId = payment.getRazorpayOrderId();
        failureReason = payment.getFailureReason();
        createdAt = payment.getCreatedAt();

        PaymentDtos.PaymentResponse paymentResponse = new PaymentDtos.PaymentResponse( id, paymentId, orderNumber, userEmail, amount, status, transactionRef, razorpayOrderId, failureReason, createdAt );

        return paymentResponse;
    }
}
