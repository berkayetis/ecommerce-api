package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.CheckoutRequest;
import com.berkayyetis.store.dtos.CheckoutResponse;
import com.berkayyetis.store.dtos.ErrorDto;
import com.berkayyetis.store.entities.Order;
import com.berkayyetis.store.exceptions.CartEmptyException;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.repositories.CartRepository;
import com.berkayyetis.store.repositories.OrderRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) {
        System.out.println("Checkout request: " + checkoutRequest);
        var cartId = checkoutRequest.getCartId();
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);

        if (cart == null) {
            throw new CartNotFoundException();
        }
        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getUser());

        orderRepository.save(order);
        cartService.clearCart(cartId);

        return new CheckoutResponse(order.getId());
    }
}
