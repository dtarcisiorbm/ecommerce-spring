package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.ProductMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ProductDataProvider implements ProductGateway {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductDataProvider(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        var entity = mapper.toEntity(product);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    @Override
    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }
    @Override
    public Optional<Product> findBySku(String sku) {
        return repository.findBySku(sku).map(mapper::toDomain);
    }
    @Override
    public Optional<Product> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    // Métodos de busca e filtro
    @Override
    public Page<Product> findByNameContaining(String name, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(name, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public Page<Product> findByCategoryId(UUID categoryId, Pageable pageable) {
        return repository.findByCategoryId(categoryId, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public Page<Product> findByFilters(String name, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
                                   Integer minStock, Boolean inStock, Pageable pageable) {
        return repository.findByFilters(name, categoryId, minPrice, maxPrice, minStock, inStock, pageable)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Product> findByCategoryId(UUID categoryId) {
        return repository.findByCategoryId(categoryId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}