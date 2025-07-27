package com.berkayyetis.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCartItem {
    @NotNull
    private Long productId;
}
