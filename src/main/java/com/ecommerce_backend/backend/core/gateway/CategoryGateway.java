package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryGateway {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    List<Category> findByParentId(UUID parentId);
    List<Category> findRootCategories();
    List<Category> findAllActive();
    Page<Category> findAll(Pageable pageable);
    void softDeleteById(UUID id);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, UUID id);
}
