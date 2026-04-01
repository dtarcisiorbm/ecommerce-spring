package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record ShoppingCartItem(
        UUID id,
        UUID shoppingCartId,
        UUID productId,
        String productName,
        String productSku,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice
) {
    public ShoppingCartItem {
        // Calcula o preço total automaticamente
        totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
