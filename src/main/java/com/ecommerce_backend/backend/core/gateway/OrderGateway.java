package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderGateway {
    Order save(Order order);
    Page<Order> findAll(Pageable pageable);
}