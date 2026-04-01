package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderGateway {
    Order save(Order order);
    Page<Order> findAll(Pageable pageable);
    Optional<Order> findById(Long id);
    Order updateStatus(Long id, OrderStatus status);
    List<Order> findByProductId(Long productId);
}