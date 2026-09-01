package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryDtos.*;
import com.ecommerce.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory", description = "Stock management and validation")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all inventory records (ADMIN only)")
    public ResponseEntity<List<InventoryResponse>> findAll() {
        log.info("Fetching all the inventor");
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get inventory for a specific product")
    public ResponseEntity<InventoryResponse> findByProductId(@PathVariable Long productId) {
        
        log.info("Fetching inventory for product ID: {}", productId);
        return ResponseEntity.ok(inventoryService.findByProductId(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create or update inventory for a product (ADMIN only)")
    public ResponseEntity<InventoryResponse> createOrUpdate(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.createOrUpdate(request));
    }

    @PostMapping("/check")
    @Operation(summary = "Check if a single product is in stock")
    public ResponseEntity<StockCheckResponse> checkStock(@Valid @RequestBody StockCheckRequest request) {
        return ResponseEntity.ok(inventoryService.checkStock(request));
    }

    @PostMapping("/check/bulk")
    @Operation(summary = "Bulk stock check for multiple products (used by Order Service)")
    public ResponseEntity<List<StockCheckResponse>> checkBulkStock(
            @Valid @RequestBody BulkStockCheckRequest request) {
        return ResponseEntity.ok(inventoryService.checkBulkStock(request));
    }

    @PostMapping("/deduct")
    @Operation(summary = "Deduct stock for multiple products (called by Order Service)")
    public ResponseEntity<Void> deductStock(@Valid @RequestBody BulkStockDeductRequest request) {
        inventoryService.deductStock(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/restock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restock a product (ADMIN only or on order cancellation)")
    public ResponseEntity<Void> restock(@Valid @RequestBody RestockRequest request) {
        inventoryService.restockProduct(request);
        return ResponseEntity.ok().build();
    }
}
