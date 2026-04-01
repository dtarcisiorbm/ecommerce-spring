package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotBlank String name,
        @NotBlank String sku,
        @NotNull @DecimalMin(value = "0.01", inclusive = false) BigDecimal price,
        @NotNull @Positive Integer stock,
        UUID categoryId
) {}
