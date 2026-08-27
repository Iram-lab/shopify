package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderDtos;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T17:04:57+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDtos.OrderItemResponse toDto(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        Long productId = null;
        String productName = null;
        BigDecimal unitPrice = null;
        Integer quantity = null;
        BigDecimal subtotal = null;

        productId = item.getProductId();
        productName = item.getProductName();
        unitPrice = item.getUnitPrice();
        quantity = item.getQuantity();
        subtotal = item.getSubtotal();

        OrderDtos.OrderItemResponse orderItemResponse = new OrderDtos.OrderItemResponse( productId, productName, unitPrice, quantity, subtotal );

        return orderItemResponse;
    }

    @Override
    public OrderDtos.OrderResponse toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        String razorpayOrderId = null;
        String razorpayKeyId = null;
        Long id = null;
        String orderNumber = null;
        String userEmail = null;
        OrderStatus status = null;
        BigDecimal totalAmount = null;
        String shippingAddress = null;
        String paymentId = null;
        List<OrderDtos.OrderItemResponse> items = null;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        razorpayOrderId = order.getRazorpayOrderId();
        razorpayKeyId = order.getRazorpayKeyId();
        id = order.getId();
        orderNumber = order.getOrderNumber();
        userEmail = order.getUserEmail();
        status = order.getStatus();
        totalAmount = order.getTotalAmount();
        shippingAddress = order.getShippingAddress();
        paymentId = order.getPaymentId();
        items = orderItemListToOrderItemResponseList( order.getItems() );
        createdAt = order.getCreatedAt();
        updatedAt = order.getUpdatedAt();

        OrderDtos.OrderResponse orderResponse = new OrderDtos.OrderResponse( id, orderNumber, userEmail, status, totalAmount, shippingAddress, paymentId, razorpayOrderId, razorpayKeyId, items, createdAt, updatedAt );

        return orderResponse;
    }

    protected List<OrderDtos.OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderDtos.OrderItemResponse> list1 = new ArrayList<OrderDtos.OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toDto( orderItem ) );
        }

        return list1;
    }
}
