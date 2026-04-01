package com.ecommerce_backend.backend.core.useCases.update;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateProductUseCase {
    private final ProductGateway productGateway;

    public UpdateProductUseCase(ProductGateway productGateway) {
        this.productGateway = productGateway;
    }

    public Product execute(UUID id, Product product) {
        // Verifica se o produto existe
        Optional<Product> existingProduct = productGateway.findById(id);
        if (existingProduct.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }

        // Verifica se o SKU já está em uso por outro produto
        if (!existingProduct.get().sku().equals(product.sku())) {
            Optional<Product> productBySku = productGateway.findBySku(product.sku());
            if (productBySku.isPresent() && !productBySku.get().id().equals(id)) {
                throw new IllegalArgumentException("Product with SKU " + product.sku() + " already exists");
            }
        }

        // Garante que o ID do produto seja o mesmo do parâmetro
        Product updatedProduct = new Product(id, product.name(), product.sku(), 
                                           product.price(), product.stock(), product.categoryId());
        
        return productGateway.save(updatedProduct);
    }
}
