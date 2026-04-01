package com.ecommerce_backend.backend.core.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        Customer customer,
        List<OrderItem> items,
        LocalDateTime createdAt,
        OrderStatus status
) {
    public BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
