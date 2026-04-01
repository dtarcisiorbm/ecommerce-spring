package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddCartItemRequest(
        UUID productId,
        @Positive(message = "Quantity must be positive") Integer quantity
) {}
