package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import com.ecommerce_backend.backend.core.useCases.CreateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptHasherAdapter implements PasswordHasherGateway {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainText) {
        return encoder.encode(plainText);
    }
}

