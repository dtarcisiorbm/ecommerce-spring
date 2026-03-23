package com.ecommerce_backend.backend.core.domain;

import java.util.Set;

public record User(
        Long id,
        String name,
        String email,
        String password, // Senha já criptografada
        Set<String> roles,
        boolean active
) {
    // Regra de negócio: validar se o e-mail é institucional, por exemplo
    public boolean isInternalUser() {
        return email.endsWith("@empresa.com");
    }
}
