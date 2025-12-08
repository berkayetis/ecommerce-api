package com.berkayyetis.store.payments;

import com.berkayyetis.store.dtos.ErrorDto;
import com.berkayyetis.store.exceptions.CartEmptyException;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.repositories.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final OrderRepository orderRepository;


    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
            return checkoutService.checkout(checkoutRequest);
    }
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody String payload, @RequestHeader Map<String, String> sigHeader) {
        checkoutService.handleWebhookEvent(new WebhookRequest(payload, sigHeader));
    }

    @ExceptionHandler({PaymentException.class})
    public ResponseEntity<?> handlePaymentException(PaymentException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
    }
}
