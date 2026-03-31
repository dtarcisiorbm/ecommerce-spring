package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.useCases.create.CreateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListProductsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(ProductGateway productGateway) {
        return new CreateProductUseCase(productGateway);
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(ProductGateway productGateway) {
        return new ListProductsUseCase(productGateway);
    }
}