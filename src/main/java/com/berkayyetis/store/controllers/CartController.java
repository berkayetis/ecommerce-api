package com.berkayyetis.store.controllers;

import com.berkayyetis.store.dtos.CartDto;
import com.berkayyetis.store.dtos.CartItemDto;
import com.berkayyetis.store.dtos.RequestCartItem;
import com.berkayyetis.store.dtos.UpdateCartItemDto;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.exceptions.ProductNotFoundException;
import com.berkayyetis.store.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carts")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> addCart(UriComponentsBuilder uriBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
    @Operation(summary = "Add item to cart")
    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addCartItem(
            UriComponentsBuilder uriBuilder,
            @Parameter(description = "ID of Cart") @PathVariable(name = "cartId") UUID cartId,
            @RequestBody RequestCartItem requestCartItem) {
        var cartItemDto = cartService.createCartItem(cartId, requestCartItem.getProductId());

        var uri = uriBuilder.path("/carts/{id}").path(requestCartItem.getProductId().toString())
                .buildAndExpand(cartId).toUri();

        return ResponseEntity.created(uri).body(cartItemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDto> getCart(@PathVariable(name = "id") UUID cartId) {
        var cartDto = cartService.getCartById(cartId);
        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable(name = "cartId") UUID cartId,
            @PathVariable(name = "productId") Long productId,
            @Valid @RequestBody UpdateCartItemDto updateCartItemDto){
        var cartItemDto = cartService.updateCartItem(cartId, productId, updateCartItemDto.getQuantity());
        return ResponseEntity.ok(cartItemDto);
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteCartItem(
            @PathVariable(name = "cartId") UUID cartId,
            @PathVariable(name = "productId") Long productId) {
        cartService.deleteCartItem(cartId, productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> deleteCart(@PathVariable(name = "cartId") UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleCartNotFoundException() {
        return ResponseEntity.badRequest().body(Map.of("error", "Cart not found"));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleProductNotFoundException() {
        return ResponseEntity.badRequest().body(Map.of("error", "Product not found"));
    }
}
