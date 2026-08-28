package com.ecommerce.inventory.saga;

import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.dto.InventoryDtos.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventorySagaListener {

    private final InventoryService inventoryService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String STOCK_REPLY_TOPIC = "saga.stock-reply";

    // ── Listen for commands from orchestrator ──────────────────────────
    @KafkaListener(topics = "saga.reserve-stock", groupId = "inventory-service-group")
    public void onSagaCommand(Map<String, Object> payload) {
        String type = determineCommandType(payload);

        if ("ReserveStockCommand".equals(type)) {
            handleReserveStock(payload);
        } else if ("ReleaseStockCommand".equals(type)) {
            handleReleaseStock(payload);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleReserveStock(Map<String, Object> payload) {
        String orderNumber = (String) payload.get("orderNumber");
        log.info("SAGA ← ReserveStockCommand: orderNumber={}", orderNumber);

        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            List<StockDeductRequest> deductions = items.stream()
                .map(i -> new StockDeductRequest(
                    Long.valueOf(i.get("productId").toString()),
                    (Integer) i.get("quantity")
                ))
                .toList();

            inventoryService.deductStock(new BulkStockDeductRequest(deductions));
            log.info("SAGA stock reserved: orderNumber={}", orderNumber);

            kafkaTemplate.send(STOCK_REPLY_TOPIC, orderNumber,
                new StockReply(orderNumber, true, null));

        } catch (Exception e) {
            log.error("SAGA stock reservation FAILED: orderNumber={}, error={}", orderNumber, e.getMessage());
            kafkaTemplate.send(STOCK_REPLY_TOPIC, orderNumber,
                new StockReply(orderNumber, false, e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleReleaseStock(Map<String, Object> payload) {
        String orderNumber = (String) payload.get("orderNumber");
        log.info("SAGA ← ReleaseStockCommand (compensation): orderNumber={}", orderNumber);

        try {
            List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
            items.forEach(i -> inventoryService.restockProduct(new RestockRequest(
                Long.valueOf(i.get("productId").toString()),
                (Integer) i.get("quantity")
            )));
            log.info("SAGA stock released (compensation): orderNumber={}", orderNumber);

            // Send compensation-complete reply
            kafkaTemplate.send(STOCK_REPLY_TOPIC, orderNumber,
                new StockReply(orderNumber + ":compensation", true, null));

        } catch (Exception e) {
            log.error("SAGA stock release FAILED: orderNumber={}, error={}", orderNumber, e.getMessage());
        }
    }

    private String determineCommandType(Map<String, Object> payload) {
        // If payload has "items" but no "amount" it's a stock command
        // ReleaseStockCommand has no "userEmail"
        if (payload.containsKey("userEmail")) return "ReserveStockCommand";
        return "ReleaseStockCommand";
    }

    // Reply record
    public record StockReply(String orderNumber, boolean success, String reason) {}
}
