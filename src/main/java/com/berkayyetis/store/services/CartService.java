package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.*;
import com.berkayyetis.store.entities.Cart;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.exceptions.ProductNotFoundException;
import com.berkayyetis.store.mapper.CartMapper;
import com.berkayyetis.store.repositories.CartRepository;
import com.berkayyetis.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    public CartDto createCart() {
        Cart cart = new Cart();
        cartRepository.save(cart);
        var cartDto = cartMapper.toDto(cart);
        return cartDto;
    }
    public CartDto getCartById(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        return cartMapper.toDto(cart);
    }
    public CartItemDto createCartItem(UUID cartId, Long productId) {
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return null;
        }

        var cartItem = cart.addCartItem(product);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }
    public CartItemDto updateCartItem(UUID cartId,Long productId ,Integer quantity) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var cartItem = cart.getCartItem(productId);

        if (cartItem == null) {
            throw new ProductNotFoundException();
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }
    public void deleteCartItem(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }
        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new ProductNotFoundException();
        }
        cart.removeCartItem(productId);
        cartRepository.save(cart);
    }
    public void clearCart(UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        cart.removeAllCartItems();
        cartRepository.save(cart);
    }
}
