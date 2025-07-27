package com.berkayyetis.store.controllers;

import com.berkayyetis.store.dtos.CheckoutRequest;
import com.berkayyetis.store.dtos.CheckoutResponse;
import com.berkayyetis.store.dtos.ErrorDto;
import com.berkayyetis.store.entities.Order;
import com.berkayyetis.store.entities.OrderItem;
import com.berkayyetis.store.entities.OrderStatus;
import com.berkayyetis.store.exceptions.CartEmptyException;
import com.berkayyetis.store.exceptions.CartNotFoundException;
import com.berkayyetis.store.repositories.CartRepository;
import com.berkayyetis.store.repositories.OrderRepository;
import com.berkayyetis.store.services.AuthService;
import com.berkayyetis.store.services.CartService;
import com.berkayyetis.store.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        var result = checkoutService.checkout(checkoutRequest);
        return result;
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
    }
}
