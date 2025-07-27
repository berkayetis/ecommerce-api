package com.berkayyetis.store.repositories;

import com.berkayyetis.store.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
