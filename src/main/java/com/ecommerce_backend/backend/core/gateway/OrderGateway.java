package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Order;

public interface OrderGateway {
    Order save(Order order);
}