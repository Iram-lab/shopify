package com.ecommerce.order.entity;

public enum OrderStatus {
    PENDING,           // order just created, saga started
    STOCK_RESERVING,   // orchestrator sent reserve-stock command
    STOCK_RESERVED,    // inventory confirmed stock reserved
    PAYMENT_PROCESSING,// orchestrator sent process-payment command
    CONFIRMED,         // payment succeeded — saga complete
    CANCELLING,        // compensation in progress
    CANCELLED          // saga compensated — order cancelled
}
