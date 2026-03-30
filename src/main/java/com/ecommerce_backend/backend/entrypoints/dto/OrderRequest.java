package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        @NotNull Long customerId,
        @NotEmpty @Valid List<OrderItemRequest> items
) {}

