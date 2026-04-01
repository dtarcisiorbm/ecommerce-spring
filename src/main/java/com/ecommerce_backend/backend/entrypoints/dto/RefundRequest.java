package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record RefundRequest(
        @NotNull(message = "Refund amount is required")
        @Positive(message = "Refund amount must be positive")
        BigDecimal amount
) {}
