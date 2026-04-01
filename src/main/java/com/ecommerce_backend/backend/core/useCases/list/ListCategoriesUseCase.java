package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ListCategoriesUseCase {
    private final CategoryGateway categoryGateway;

    public ListCategoriesUseCase(CategoryGateway categoryGateway) {
        this.categoryGateway = categoryGateway;
    }

    public Page<Category> execute(Pageable pageable) {
        return categoryGateway.findAll(pageable);
    }

    public List<Category> executeActive() {
        return categoryGateway.findAllActive();
    }

    public List<Category> executeRootCategories() {
        return categoryGateway.findRootCategories();
    }

    public List<Category> executeByParent(UUID parentId) {
        return categoryGateway.findByParentId(parentId);
    }
}
