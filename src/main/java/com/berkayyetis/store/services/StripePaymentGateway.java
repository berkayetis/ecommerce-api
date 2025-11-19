package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.CheckoutResponse;
import com.berkayyetis.store.entities.Order;
import com.berkayyetis.store.entities.OrderItem;
import com.berkayyetis.store.entities.PaymentStatus;
import com.berkayyetis.store.exceptions.PaymentException;
import com.berkayyetis.store.repositories.OrderRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StripePaymentGateway implements PaymentGateway {
    private final OrderRepository orderRepository;

    @Value("${stripe.webUrl}")
    private String webUrl;

    @Value("${stripe.webhookSecretKey}")
    private String endpointSecret;

    @Override
    public CheckoutSession createCheckoutSession(Order order){
        try {
            //checkout
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(webUrl+"/checkout-success?orderId="+order.getId())
                    .setCancelUrl(webUrl+"/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString());

            order.getItems().forEach(item ->{
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(createPriceData(item))
                        .build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());

        }catch (StripeException e) {
            System.out.println(e.getMessage());
            throw new PaymentException("PaymentException Error");
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest webhookRequest) {
        try {
            var event = Webhook.constructEvent(webhookRequest.getPayload(),
                    webhookRequest.getHeaders().get("stripe-signature"),
                    endpointSecret);

            var orderId = extractOrderId(event);

            return switch (event.getType()) {
                case "payment_intent.succeeded" -> Optional.of(new PaymentResult(orderId, PaymentStatus.PAID));
                case "payment_intent.payment_failed" -> Optional.of(new PaymentResult(orderId, PaymentStatus.FAILED));
                default -> Optional.empty();
            };
        } catch (SignatureVerificationException e) {
            // refuse
            throw new PaymentException("Signature verification failed");
        }
    }

    private Long extractOrderId(Event event){
        var stripeObject = event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new PaymentException("Could not extracted order id by Stripe event. Check the sdk and api version. "));

        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("try")
                .setUnitAmountDecimal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }


}
