package com.berkayyetis.store.mapper;

import com.berkayyetis.store.dtos.CartDto;
import com.berkayyetis.store.dtos.CartItemDto;
import com.berkayyetis.store.entities.Cart;
import com.berkayyetis.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
