package com.ecommerce_backend.backend.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record Payment(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentMethod method,
        PaymentStatus status,
        String transactionId,
        String gatewayResponse,
        LocalDateTime createdAt,
        LocalDateTime processedAt
) {
    public Payment {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
