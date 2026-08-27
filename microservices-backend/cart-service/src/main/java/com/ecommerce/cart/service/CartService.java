package com.ecommerce.cart.service;

import com.ecommerce.cart.client.InventoryClient;
import com.ecommerce.cart.client.InventoryClient.StockCheckRequest;
import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.client.ProductClient.ProductResponse;
import com.ecommerce.cart.dto.CartDtos.*;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.exception.ResourceNotFoundException;
import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final CartMapper mapper;

    public CartResponse getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapper.toDto(cart);
    }

    @Transactional
    public CartResponse addItem(String userEmail, AddToCartRequest request) {
        // 1. Fetch product details from Product Service
        ProductResponse product = productClient.getProduct(request.productId());
        if (!product.active()) {
            throw new IllegalArgumentException("Product is not available: " + request.productId());
        }

        // 2. Validate stock from Inventory Service
        var stockCheck = inventoryClient.checkStock(
            new StockCheckRequest(request.productId(), request.quantity()));
        if (!stockCheck.inStock()) {
            throw new IllegalArgumentException(
                "Insufficient stock. Available: " + stockCheck.availableStock());
        }

        // 3. Add or update cart item
        Cart cart = getOrCreateCart(userEmail);
        cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId())
            .ifPresentOrElse(
                existing -> existing.setQuantity(existing.getQuantity() + request.quantity()),
                () -> {
                    CartItem newItem = CartItem.builder()
                        .cart(cart)
                        .productId(product.id())
                        .productName(product.name())
                        .unitPrice(product.price())
                        .imageUrl(product.imageUrl())
                        .quantity(request.quantity())
                        .build();
                    cart.getItems().add(newItem);
                }
            );

        return mapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(String userEmail, Long productId, UpdateCartItemRequest request) {
        Cart cart = getCartOrThrow(userEmail);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
            .orElseThrow(() -> new ResourceNotFoundException("Item not in cart: " + productId));

        // Validate new quantity against stock
        var stockCheck = inventoryClient.checkStock(
            new StockCheckRequest(productId, request.quantity()));
        if (!stockCheck.inStock()) {
            throw new IllegalArgumentException(
                "Insufficient stock. Available: " + stockCheck.availableStock());
        }

        item.setQuantity(request.quantity());
        return mapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(String userEmail, Long productId) {
        Cart cart = getCartOrThrow(userEmail);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        return mapper.toDto(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String userEmail) {
        cartRepository.findByUserEmailWithItems(userEmail).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
            log.info("Cart cleared for user: {}", userEmail);
        });
    }

    public CartSummary getCartSummary(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return mapper.toSummary(cart);
    }

    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmailWithItems(userEmail)
            .orElseGet(() -> cartRepository.save(
                Cart.builder().userEmail(userEmail).build()
            ));
    }

    private Cart getCartOrThrow(String userEmail) {
        return cartRepository.findByUserEmailWithItems(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userEmail));
    }
}
