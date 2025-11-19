package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.CheckoutRequest;
import com.berkayyetis.store.dtos.CheckoutResponse;
import com.berkayyetis.store.entities.Order;
import com.berkayyetis.store.entities.PaymentStatus;
import com.berkayyetis.store.exceptions.CartEmptyException;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.exceptions.PaymentException;
import com.berkayyetis.store.repositories.CartRepository;
import com.berkayyetis.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;


    @Value("${stripe.secretKey}")
    private String stripeApiKey;


    public CheckoutResponse checkout(CheckoutRequest checkoutRequest) throws PaymentException {
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

        try {
            var session = paymentGateway.createCheckoutSession(order);

            cartService.clearCart(cartId);

            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        }catch (PaymentException e) {
            System.out.println(e.getMessage());
            orderRepository.delete(order);
            throw e;
        }
    }

    public void handleWebhookEvent(WebhookRequest webhookRequest){
        paymentGateway
                .parseWebhookRequest(webhookRequest)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(paymentResult.getOrderId()).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });
    }
}
