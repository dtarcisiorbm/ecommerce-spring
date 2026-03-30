package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;

public class CreateProductUseCase {
    private final ProductGateway productGateway;

    public CreateProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product execute(Product product) {
        // Regra: Não permitir SKUs duplicados
        if (productGateway.findBySku(product.sku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + product.sku() + " already exists");
        }
        return productGateway.save(product);
    }
}