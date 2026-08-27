package com.ecommerce.inventory.config;

import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (inventoryRepository.count() > 0) {
            log.info("Inventory already seeded, skipping...");
            return;
        }

        log.info("Seeding inventory for 50 products...");

        // Product IDs 1-50 (5 categories x 10 products each)
        List<Inventory> inventories = LongStream.rangeClosed(1, 50)
            .mapToObj(productId -> Inventory.builder()
                .productId(productId)
                .quantityAvailable(100)
                .reservedQuantity(0)
                .build())
            .toList();

        inventoryRepository.saveAll(inventories);
        log.info("Inventory seeded: 50 products with 100 units each.");
    }
}
