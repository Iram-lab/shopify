package com.ecommerce.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class InventoryDtos {

    public record InventoryRequest(
        @NotNull Long productId,
        @NotNull @Min(0) Integer quantityAvailable
    ) {}

    public record InventoryResponse(
        Long id,
        Long productId,
        Integer quantityAvailable,
        Integer reservedQuantity,
        Integer availableStock
    ) {}

    public record StockCheckRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
    ) {}

    public record StockCheckResponse(
        Long productId,
        boolean inStock,
        Integer availableStock
    ) {}

    public record BulkStockCheckRequest(
        @NotNull List<StockCheckRequest> items
    ) {}

    public record StockDeductRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
    ) {}

    public record BulkStockDeductRequest(
        @NotNull List<StockDeductRequest> items
    ) {}

    public record RestockRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
    ) {}
}
