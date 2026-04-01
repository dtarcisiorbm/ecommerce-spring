package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductFilterRequest(
        String name,
        String sku,
        UUID categoryId,
        @DecimalMin(value = "0.01", inclusive = false) BigDecimal minPrice,
        @DecimalMin(value = "0.01", inclusive = false) BigDecimal maxPrice,
        @Positive Integer minStock,
        Boolean inStock
) {}
