package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(
        Long productId,
        @Positive(message = "Quantity must be positive") Integer quantity
) {}
