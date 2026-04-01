package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Category;
import com.ecommerce_backend.backend.core.gateway.CategoryGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.CategoryMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CategoryDataProvider implements CategoryGateway {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryDataProvider(CategoryRepository repository, CategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Category save(Category category) {
        var entity = mapper.toEntity(category);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Category> findByParentId(UUID parentId) {
        return repository.findByParentId(parentId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findRootCategories() {
        return repository.findByParentIdIsNull()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> findAllActive() {
        return repository.findAllActive()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void softDeleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        repository.softDeleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return repository.existsByNameAndIdNot(name, id);
    }
}
