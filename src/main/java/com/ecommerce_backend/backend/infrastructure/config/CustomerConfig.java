package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import com.ecommerce_backend.backend.core.useCases.create.AuthenticateCustomerUseCase;
import com.ecommerce_backend.backend.core.useCases.create.CreateCustomerUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomerConfig {
    @Bean
    public CreateCustomerUseCase createCustomerUseCase(CustomerGateway customerGateway, PasswordHasherGateway hasher) {
        return new CreateCustomerUseCase(customerGateway, hasher);
    }
    @Bean
    public AuthenticateCustomerUseCase authenticateCustomerUseCase(CustomerGateway customerGateway, PasswordHasherGateway hasher) {
        return new AuthenticateCustomerUseCase(customerGateway, hasher);
    }


}