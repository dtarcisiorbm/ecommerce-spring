package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;

public record Product(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stock
) {
    // Exemplo de regra de negócio no domínio
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }
}
