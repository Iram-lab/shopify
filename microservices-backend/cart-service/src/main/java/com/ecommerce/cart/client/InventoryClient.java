package com.ecommerce.cart.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", fallback = InventoryClient.InventoryClientFallback.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/check")
    @CircuitBreaker(name = "inventory-service", fallbackMethod = "checkStockFallback")
    StockCheckResponse checkStock(@RequestBody StockCheckRequest request);

    record StockCheckRequest(Long productId, Integer quantity) {}

    record StockCheckResponse(Long productId, boolean inStock, Integer availableStock) {}

    @Component
    @Slf4j
    class InventoryClientFallback implements InventoryClient {
        @Override
        public StockCheckResponse checkStock(StockCheckRequest request) {
            log.warn("Inventory service unavailable. Allowing add to cart for productId={}", request.productId());
            // Fail open: allow adding to cart when inventory is unreachable
            return new StockCheckResponse(request.productId(), true, 999);
        }
    }
}
