package com.berkayyetis.store.dtos;

import com.berkayyetis.store.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private List<CartItemDto> items = new ArrayList<>();
}
