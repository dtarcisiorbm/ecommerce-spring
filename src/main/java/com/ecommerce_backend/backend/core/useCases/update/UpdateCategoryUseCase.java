package com.ecommerce_backend.backend.core.useCases.update;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateCategoryUseCase {
    private final CategoryGateway categoryGateway;

    public UpdateCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Category execute(Category category) {
        // Validações
        if (category.name() == null || category.name().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        // Verifica se a categoria existe
        Optional<Category> existingCategory = categoryGateway.findById(category.id());
        if (existingCategory.isEmpty()) {
            throw new IllegalArgumentException("Category not found with id: " + category.id());
        }

        // Verifica se já existe outra categoria com mesmo nome
        if (categoryGateway.existsByNameAndIdNot(category.name(), category.id())) {
            throw new IllegalArgumentException("Category with name '" + category.name() + "' already exists");
        }

        // Se for subcategoria, verifica se a categoria pai existe e está ativa
        if (category.parentId() != null) {
            var parentCategory = categoryGateway.findById(category.parentId());
            if (parentCategory.isEmpty()) {
                throw new IllegalArgumentException("Parent category not found with id: " + category.parentId());
            }
            
            // Impede ciclo de referência (categoria não pode ser pai de si mesma)
            if (category.parentId().equals(category.id())) {
                throw new IllegalArgumentException("Category cannot be parent of itself");
            }
        }

        return categoryGateway.save(category);
    }
}
