package com.ecommerce_backend.backend.core.useCases.delete;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteCategoryUseCase {
    private final CategoryGateway categoryGateway;

    public DeleteCategoryUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public void execute(UUID id) {
        // Verifica se a categoria existe
        Optional<Category> category = categoryGateway.findById(id);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        // Verifica se existem subcategorias ativas
        List<Category> subcategories = categoryGateway.findByParentId(id);
        boolean hasActiveSubcategories = subcategories.stream()
                .anyMatch(sub -> sub.active());
        
        if (hasActiveSubcategories) {
            throw new IllegalStateException(
                "Cannot delete category: it has active subcategories. " +
                "Please delete or deactivate subcategories first."
            );
        }

        // Realiza soft delete mantendo o histórico
        categoryGateway.softDeleteById(id);
    }
}
