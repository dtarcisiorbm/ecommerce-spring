package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;

public record OrderItem(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}