package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record Product(
        Long id,
        String name,
        String sku,
        BigDecimal price,
        Integer stock,
        UUID categoryId
) {
    // Exemplo de regra de negócio no domínio
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }
}
