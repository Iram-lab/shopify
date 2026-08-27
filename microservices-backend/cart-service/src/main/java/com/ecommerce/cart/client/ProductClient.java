package com.ecommerce.cart.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "product-service", fallback = ProductClient.ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductFallback")
    ProductResponse getProduct(@PathVariable Long id);

    record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        String imageUrl,
        boolean active
    ) {}

    @Component
    @Slf4j
    class ProductClientFallback implements ProductClient {
        @Override
        public ProductResponse getProduct(Long id) {
            log.warn("Product service unavailable. Falling back for productId={}", id);
            return new ProductResponse(id, "Product Unavailable", BigDecimal.ZERO, null, false);
        }
    }
}
