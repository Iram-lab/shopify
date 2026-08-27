package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryDtos.*;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.exception.InsufficientStockException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.mapper.InventoryMapper;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper mapper;

    public InventoryResponse findByProductId(Long productId) {
        return mapper.toDto(getOrThrow(productId));
    }

    public List<InventoryResponse> findAll() {
        return inventoryRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Transactional
    public InventoryResponse createOrUpdate(InventoryRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.productId())
            .orElseGet(() -> mapper.toEntity(request));
        inventory.setQuantityAvailable(request.quantityAvailable());
        return mapper.toDto(inventoryRepository.save(inventory));
    }

    /**
     * Single product stock check — used by Cart Service
     */
    public StockCheckResponse checkStock(StockCheckRequest request) {
        return inventoryRepository.findByProductId(request.productId())
            .map(inv -> new StockCheckResponse(
                request.productId(),
                inv.getAvailableStock() >= request.quantity(),
                inv.getAvailableStock()
            ))
            .orElse(new StockCheckResponse(request.productId(), false, 0));
    }

    /**
     * Bulk stock check — used by Order Service before placing an order
     */
    public List<StockCheckResponse> checkBulkStock(BulkStockCheckRequest request) {
        return request.items().stream().map(this::checkStock).toList();
    }

    /**
     * Bulk stock deduction — called by Order Service on order confirmation.
     * Uses pessimistic locking per product to prevent overselling.
     * If ANY item is out of stock, the entire transaction rolls back.
     */
    @Transactional
    public void deductStock(BulkStockDeductRequest request) {
        for (StockDeductRequest item : request.items()) {
            Inventory inventory = inventoryRepository
                .findByProductIdWithLock(item.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Inventory not found for product: " + item.productId()));

            if (inventory.getAvailableStock() < item.quantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product " + item.productId() +
                    ". Available: " + inventory.getAvailableStock() +
                    ", Requested: " + item.quantity());
            }

            inventory.setQuantityAvailable(inventory.getQuantityAvailable() - item.quantity());
            inventoryRepository.save(inventory);
            log.info("Stock deducted: productId={}, qty={}, remaining={}",
                item.productId(), item.quantity(), inventory.getAvailableStock());
        }
    }

    /**
     * Restock — called when an order is cancelled or by admin
     */
    @Transactional
    public void restockProduct(RestockRequest request) {
        Inventory inventory = getOrThrow(request.productId());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + request.quantity());
        inventoryRepository.save(inventory);
        log.info("Restocked: productId={}, qty={}, total={}",
            request.productId(), request.quantity(), inventory.getQuantityAvailable());
    }

    private Inventory getOrThrow(Long productId) {
        return inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Inventory not found for product: " + productId));
    }
}
