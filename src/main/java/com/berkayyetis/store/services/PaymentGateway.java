package com.berkayyetis.store.services;

import com.berkayyetis.store.entities.Order;

public interface PaymentGateway {
    CheckoutSession createCheckoutSession(Order order);
}
