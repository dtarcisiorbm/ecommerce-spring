package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ListOrdersUseCase {
    private final OrderGateway orderGateway;

    public ListOrdersUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public Page<Order> execute(Pageable pageable) {
        return orderGateway.findAll(pageable);
    }
}