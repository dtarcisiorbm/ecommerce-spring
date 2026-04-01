package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductGateway {
    Product save(Product product);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(UUID id);
    Page<Product> findAll(Pageable pageable);
    void deleteById(UUID id);
    
    // Métodos de busca e filtro
    Page<Product> findByNameContaining(String name, Pageable pageable);
    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
    Page<Product> findByFilters(String name, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
                               Integer minStock, Boolean inStock, Pageable pageable);
    List<Product> findByCategoryId(UUID categoryId);
}