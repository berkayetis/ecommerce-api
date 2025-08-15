package com.berkayyetis.store.mapper;

import com.berkayyetis.store.dtos.OrderDto;
import com.berkayyetis.store.entities.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
