package com.ecommerce.order.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service", fallback = InventoryClient.InventoryClientFallback.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/check/bulk")
    List<StockCheckResponse> checkBulkStock(@RequestBody BulkStockCheckRequest request);

    @PostMapping("/api/inventory/deduct")
    void deductStock(@RequestBody BulkStockDeductRequest request);

    @PostMapping("/api/inventory/restock")
    void restockProduct(@RequestBody RestockRequest request);

    record StockCheckRequest(Long productId, Integer quantity) {}
    record StockCheckResponse(Long productId, boolean inStock, Integer availableStock) {}
    record BulkStockCheckRequest(List<StockCheckRequest> items) {}
    record StockDeductRequest(Long productId, Integer quantity) {}
    record BulkStockDeductRequest(List<StockDeductRequest> items) {}
    record RestockRequest(Long productId, Integer quantity) {}

    @Component
    @Slf4j
    class InventoryClientFallback implements InventoryClient {

        @Override
        public List<StockCheckResponse> checkBulkStock(BulkStockCheckRequest request) {
            log.warn("Inventory service unavailable. Circuit breaker OPEN. Returning out-of-stock fallback.");
            // Fail safe: mark all items as out of stock
            return request.items().stream()
                .map(item -> new StockCheckResponse(item.productId(), false, 0))
                .toList();
        }

        @Override
        public void deductStock(BulkStockDeductRequest request) {
            log.error("Inventory service unavailable. Stock deduction FAILED for {} items.",
                request.items().size());
            throw new RuntimeException("Inventory service is currently unavailable. Please try again.");
        }

        @Override
        public void restockProduct(RestockRequest request) {
            log.error("Inventory service unavailable. Restock FAILED for productId={}",
                request.productId());
        }
    }
}
