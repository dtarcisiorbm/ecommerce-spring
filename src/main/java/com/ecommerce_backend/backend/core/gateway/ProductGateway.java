package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Product;
import java.util.Optional;

public interface ProductGateway {
    Product save(Product product);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(Long id);
}