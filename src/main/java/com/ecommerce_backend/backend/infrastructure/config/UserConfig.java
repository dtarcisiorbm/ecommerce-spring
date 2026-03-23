package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import com.ecommerce_backend.backend.core.useCases.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Bean do UseCase
@Configuration
public class UserConfig {
    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordHasherGateway hasher) {
        return new CreateUserUseCase(userGateway, hasher);
    }
}
