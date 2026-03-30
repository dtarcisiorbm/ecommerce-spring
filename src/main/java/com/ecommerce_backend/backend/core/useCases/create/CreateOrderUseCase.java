package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;

import java.time.LocalDateTime;

public class CreateOrderUseCase {
    private final OrderGateway orderGateway;
    private final ProductGateway productGateway;

    public CreateOrderUseCase(OrderGateway orderGateway, ProductGateway productGateway) {
        this.orderGateway = orderGateway;
        this.productGateway = productGateway;
    }

    public Order execute(Order order) {
        // 1. Validar estoque de cada item
        order.items().forEach(item -> {
            var product = productGateway.findById(item.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId()));

            if (!product.hasStock(item.quantity())) {
                throw new IllegalStateException("Insufficient stock for product: " + product.name());
            }
        });

        // 2. Preparar a ordem com status inicial e data
        Order orderToSave = new Order(
                null,
                order.customer(),
                order.items(),
                LocalDateTime.now(),
                OrderStatus.PENDING
        );

        // 3. Persistir
        return orderGateway.save(orderToSave);
    }
}