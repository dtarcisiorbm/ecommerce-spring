package com.ecommerce_backend.backend.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShoppingCart(
        UUID id,
        UUID customerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ShoppingCart {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
}
