package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentId(String paymentId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    List<Payment> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    boolean existsByOrderNumber(String orderNumber);
}
