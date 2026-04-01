package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderGateway {
    Order save(Order order);
    Page<Order> findAll(Pageable pageable);
    Optional<Order> findById(UUID id);
    Order updateStatus(UUID id, OrderStatus status);
    List<Order> findByProductId(UUID productId);
}