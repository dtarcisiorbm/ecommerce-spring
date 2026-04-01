package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemRequest(
        @NotNull UUID productId,
        @NotNull Integer quantity,
        @NotNull BigDecimal unitPrice // Nota: Em produção, o preço idealmente é buscado no banco para evitar fraudes.
) {}
