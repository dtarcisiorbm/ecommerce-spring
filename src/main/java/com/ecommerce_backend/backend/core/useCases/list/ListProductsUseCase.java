package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ListProductsUseCase {
    private final ProductGateway productGateway;

    public ListProductsUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Page<Product> execute(Pageable pageable) {
        return productGateway.findAll(pageable);
    }
}