package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateCategoryUseCase {
    private final CategoryGateway categoryGateway;

    public CreateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {
        // Validações
        if (category.name() == null || category.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        // Verifica se já existe categoria com mesmo nome
        if (categoryGateway.existsByName(category.name())) {
            throw new IllegalArgumentException("Category with name '" + category.name() + "' already exists");
        }

        // Se for subcategoria, verifica se a categoria pai existe e está ativa
        if (category.parentId() != null) {
            var parentCategory = categoryGateway.findById(category.parentId());
            if (parentCategory.isEmpty()) {
                throw new IllegalArgumentException("Parent category not found with id: " + category.parentId());
            }
        }

        return categoryGateway.save(category);
    }
}
