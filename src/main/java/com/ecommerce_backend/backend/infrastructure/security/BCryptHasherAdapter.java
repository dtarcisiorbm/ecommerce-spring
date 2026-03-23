package com.ecommerce_backend.backend.infrastructure.security;

import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptHasherAdapter implements PasswordHasherGateway {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainText) {
        // Transforma "senha123" em "$2a$10$..."
        return encoder.encode(plainText);
    }

    @Override
    public boolean matches(String plainText, String hashedText) {
        // Verifica se a senha enviada bate com o hash do banco
        return encoder.matches(plainText, hashedText);
    }
}
