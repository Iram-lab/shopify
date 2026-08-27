package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDtos {

    public record CategoryRequest(
        @NotBlank String name,
        String description
    ) {}

    public record CategoryResponse(
        Long id,
        String name,
        String description
    ) {}

    public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        String imageUrl,
        String brand,
        @NotNull Long categoryId
    ) {}

    public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String brand,
        boolean active,
        CategoryResponse category,
        LocalDateTime createdAt
    ) {}

    public record ProductSummary(
        Long id,
        String name,
        BigDecimal price,
        String imageUrl,
        String brand,
        String categoryName
    ) {}

    public record PagedResponse<T>(
        java.util.List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
    ) {}
}
