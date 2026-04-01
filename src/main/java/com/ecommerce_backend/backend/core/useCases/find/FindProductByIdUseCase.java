package com.ecommerce_backend.backend.core.useCases.find;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FindProductByIdUseCase {
    private final ProductGateway productGateway;

    public FindProductByIdUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Optional<Product> execute(Long id) {
        return productGateway.findById(id);
    }
}
