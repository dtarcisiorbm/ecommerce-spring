package com.ecommerce_backend.backend.core.domain;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Order(
        Long id,
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
