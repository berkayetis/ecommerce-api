package com.berkayyetis.store.services;

import com.berkayyetis.store.dtos.OrderDto;
import com.berkayyetis.store.exceptions.OrderNotFoundException;
import com.berkayyetis.store.mapper.OrderMapper;
import com.berkayyetis.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getUser();
        var orders = orderRepository.getOrdersByCustomer(user)
                .stream()
                .map(orderMapper::toDto)
                .toList();

        return orders;
    }

    public OrderDto getOrder(Long id) {
        var order = orderRepository.getOrderWithItems(id).orElseThrow(OrderNotFoundException::new);

        var user = authService.getUser();
        if(!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        return orderMapper.toDto(order);
    }
}
