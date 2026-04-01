package com.ecommerce_backend.backend.core.useCases.search;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.entrypoints.dto.ProductFilterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class SearchProductsUseCase {
    private final ProductGateway productGateway;

    public SearchProductsUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Page<Product> execute(ProductFilterRequest filters, Pageable pageable) {
        // Se não houver filtros, retorna todos os produtos
        if (filters == null || isAllFiltersEmpty(filters)) {
            return productGateway.findAll(pageable);
        }

        // Aplica filtros específicos
        return productGateway.findByFilters(
                filters.name(),
                filters.categoryId(),
                filters.minPrice(),
                filters.maxPrice(),
                filters.minStock(),
                filters.inStock(),
                pageable
        );
    }

    public Page<Product> searchByName(String name, Pageable pageable) {
        return productGateway.findByNameContaining(name, pageable);
    }

    public Page<Product> searchByCategory(UUID categoryId, Pageable pageable) {
        return productGateway.findByCategoryId(categoryId, pageable);
    }

    private boolean isAllFiltersEmpty(ProductFilterRequest filters) {
        return (filters.name() == null || filters.name().trim().isEmpty()) &&
               filters.categoryId() == null &&
               filters.minPrice() == null &&
               filters.maxPrice() == null &&
               filters.minStock() == null &&
               filters.inStock() == null;
    }
}
