package com.ecommerce_backend.backend.core.useCases;

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

    public User execute(String email, String password) {
        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Altere de user.getPassword() para user.password()
        if (!hasher.matches(password, user.password())) {
            throw new IllegalArgumentException("Invalid password");
        }

        if (!user.active()) {
            throw new IllegalArgumentException("User is not active");
        }
        return user;
    }
}
