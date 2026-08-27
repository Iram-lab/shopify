package com.ecommerce.cart.mapper;

import com.ecommerce.cart.dto.CartDtos.*;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "subtotal", expression = "java(item.getSubtotal())")
    CartItemResponse toDto(CartItem item);

    @Mapping(target = "totalItems", expression = "java(cart.getTotalItems())")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartResponse toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartSummary toSummary(Cart cart);
}
