package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartDtos;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T15:49:31+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartDtos.CartItemResponse toDto(CartItem item) {
        if ( item == null ) {
            return null;
        }

        Long id = null;
        Long productId = null;
        String productName = null;
        BigDecimal unitPrice = null;
        String imageUrl = null;
        Integer quantity = null;

        id = item.getId();
        productId = item.getProductId();
        productName = item.getProductName();
        unitPrice = item.getUnitPrice();
        imageUrl = item.getImageUrl();
        quantity = item.getQuantity();

        BigDecimal subtotal = item.getSubtotal();

        CartDtos.CartItemResponse cartItemResponse = new CartDtos.CartItemResponse( id, productId, productName, unitPrice, imageUrl, quantity, subtotal );

        return cartItemResponse;
    }

    @Override
    public CartDtos.CartResponse toDto(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        Long id = null;
        String userEmail = null;
        List<CartDtos.CartItemResponse> items = null;
        LocalDateTime updatedAt = null;

        id = cart.getId();
        userEmail = cart.getUserEmail();
        items = cartItemListToCartItemResponseList( cart.getItems() );
        updatedAt = cart.getUpdatedAt();

        int totalItems = cart.getTotalItems();
        BigDecimal totalPrice = cart.getTotalPrice();

        CartDtos.CartResponse cartResponse = new CartDtos.CartResponse( id, userEmail, items, totalItems, totalPrice, updatedAt );

        return cartResponse;
    }

    @Override
    public CartDtos.CartSummary toSummary(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        String userEmail = null;
        List<CartDtos.CartItemResponse> items = null;

        userEmail = cart.getUserEmail();
        items = cartItemListToCartItemResponseList( cart.getItems() );

        BigDecimal totalPrice = cart.getTotalPrice();

        CartDtos.CartSummary cartSummary = new CartDtos.CartSummary( userEmail, items, totalPrice );

        return cartSummary;
    }

    protected List<CartDtos.CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartDtos.CartItemResponse> list1 = new ArrayList<CartDtos.CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toDto( cartItem ) );
        }

        return list1;
    }
}
