package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
        UUID id,
        UUID productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}