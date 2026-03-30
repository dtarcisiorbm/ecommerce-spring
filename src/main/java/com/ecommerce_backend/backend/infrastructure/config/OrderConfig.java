package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.useCases.create.CreateOrderUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {
    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderGateway orderGateway, ProductGateway productGateway) {
        return new CreateOrderUseCase(orderGateway, productGateway);
    }
}
