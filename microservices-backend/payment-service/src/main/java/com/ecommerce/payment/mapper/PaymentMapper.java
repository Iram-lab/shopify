package com.ecommerce.payment.mapper;

import com.ecommerce.payment.dto.PaymentDtos.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toDto(Payment payment);
}
