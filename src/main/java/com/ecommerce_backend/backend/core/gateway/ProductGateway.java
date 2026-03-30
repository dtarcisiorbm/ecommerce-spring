package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductGateway {
    Product save(Product product);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(Long id);
    Page<Product> findAll(Pageable pageable);
}