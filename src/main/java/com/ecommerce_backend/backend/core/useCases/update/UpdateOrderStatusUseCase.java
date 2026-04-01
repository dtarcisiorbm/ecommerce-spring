package com.ecommerce_backend.backend.core.useCases.update;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderStatus;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UpdateOrderStatusUseCase {
    private final OrderGateway orderGateway;

    public UpdateOrderStatusUseCase(OrderGateway orderGateway) {
        this.orderGateway = orderGateway;
    }

    public Order execute(Long id, OrderStatus newStatus) {
        // Verifica se o pedido existe
        Order order = orderGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));

        // Valida as transições de status permitidas
        if (!isValidStatusTransition(order.status(), newStatus)) {
            throw new IllegalArgumentException(
                String.format("Invalid status transition from %s to %s", order.status(), newStatus)
            );
        }

        return orderGateway.updateStatus(id, newStatus);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Status atual não pode ser alterado se já for CANCELED ou DELIVERED
        if (currentStatus == OrderStatus.CANCELED || currentStatus == OrderStatus.DELIVERED) {
            return false;
        }

        // Permite cancelar apenas pedidos PENDING ou PAID
        if (newStatus == OrderStatus.CANCELED) {
            return currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.PAID;
        }

        // Fluxo normal: PENDING -> PAID -> SHIPPED -> DELIVERED
        List<OrderStatus> validProgression = Arrays.asList(
            OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED
        );

        int currentIndex = validProgression.indexOf(currentStatus);
        int newIndex = validProgression.indexOf(newStatus);

        return newIndex == currentIndex + 1;
    }
}
