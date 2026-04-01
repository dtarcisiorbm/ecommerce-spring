package com.ecommerce_backend.backend.entrypoints.dto;

import com.ecommerce_backend.backend.core.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
    @NotNull(message = "Status is required") 
    OrderStatus status
) {}
