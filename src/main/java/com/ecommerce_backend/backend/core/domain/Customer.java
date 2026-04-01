package com.ecommerce_backend.backend.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Customer(
        UUID id,
        String fullName,
        String email,
        String taxId,
        String password,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Customer {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    public Customer withActive(boolean active) {
        return new Customer(id, fullName, email, taxId, password, active, createdAt, LocalDateTime.now());
    }
}