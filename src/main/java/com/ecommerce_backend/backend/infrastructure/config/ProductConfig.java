package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.useCases.create.CreateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.delete.DeleteProductUseCase;
import com.ecommerce_backend.backend.core.useCases.find.FindProductByIdUseCase;
import com.ecommerce_backend.backend.core.useCases.list.ListProductsUseCase;
import com.ecommerce_backend.backend.core.useCases.update.UpdateProductUseCase;
import com.ecommerce_backend.backend.core.useCases.search.SearchProductsUseCase;
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

    @Bean
    public FindProductByIdUseCase findProductByIdUseCase(ProductGateway productGateway) {
        return new FindProductByIdUseCase(productGateway);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductGateway productGateway) {
        return new UpdateProductUseCase(productGateway);
    }

    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductGateway productGateway, OrderGateway orderGateway) {
        return new DeleteProductUseCase(productGateway, orderGateway);
    }
    
    @Bean
    public SearchProductsUseCase searchProductsUseCase(ProductGateway productGateway) {
        return new SearchProductsUseCase(productGateway);
    }
}