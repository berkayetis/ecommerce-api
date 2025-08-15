package com.berkayyetis.store.controllers;

import com.berkayyetis.store.dtos.ErrorDto;
import com.berkayyetis.store.dtos.OrderDto;
import com.berkayyetis.store.exceptions.OrderNotFoundException;
import com.berkayyetis.store.mapper.OrderMapper;
import com.berkayyetis.store.repositories.OrderRepository;
import com.berkayyetis.store.services.AuthService;
import com.berkayyetis.store.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable("orderId") Long id) {
        return orderService.getOrder(id);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Void> handleOrderNotFound() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorDto(ex.getMessage()));
    }
}
