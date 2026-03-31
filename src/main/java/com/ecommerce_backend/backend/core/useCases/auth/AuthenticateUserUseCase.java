package com.ecommerce_backend.backend.core.useCases.auth;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;

public class AuthenticateUserUseCase {
    private final UserGateway userGateway;
    private final PasswordHasherGateway hasher;


    public AuthenticateUserUseCase(UserGateway userGateway, PasswordHasherGateway hasher) {
        this.userGateway = userGateway;
        this.hasher = hasher;
    }

    // Local: src/main/java/com/ecommerce_backend/backend/core/useCases/AuthenticateUserUseCase.java
    public User execute(String email, String password) {
        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas.")); //

        if (!hasher.matches(password, user.password())) { //
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        if (!user.active()) { //
            throw new IllegalStateException("Esta conta está desativada.");
        }
        return user;
    }
}
