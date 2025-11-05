package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.CheckoutResponse;
import com.berkayyetis.store.entities.Order;
import com.berkayyetis.store.exceptions.PaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripePaymentGateway implements PaymentGateway {
    @Value("${stripe.webUrl}")
    private String webUrl;

    @Override
    public CheckoutSession createCheckoutSession(Order order){
        try {
            //checkout
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(webUrl+"/checkout-success?orderId="+order.getId())
                    .setCancelUrl(webUrl+"/checkout-cancel");

            order.getItems().forEach(item ->{
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("try")
                                        .setUnitAmountDecimal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(100)))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                        ).build()
                        ).build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());

        }catch (StripeException e) {
            System.out.println(e.getMessage());
            throw new PaymentException();
        }
    }
}
