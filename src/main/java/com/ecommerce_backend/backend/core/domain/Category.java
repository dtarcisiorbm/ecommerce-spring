package com.ecommerce_backend.backend.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Category(
        UUID id,
        String name,
        String description,
        UUID parentId,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Category {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }
    
    public Category withActive(boolean active) {
        return new Category(id, name, description, parentId, active, createdAt, LocalDateTime.now());
    }
    
    public boolean isRootCategory() {
        return parentId == null;
    }
    
    public boolean isSubcategory() {
        return parentId != null;
    }
}
