package com.ecommerce_backend.backend.core.useCases;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;

import java.util.Collection;
import java.util.Collections;

public class CreateUserUseCase {
    private final UserGateway userGateway;

    private final PasswordHasherGateway passwordHasherGateway;

    public CreateUserUseCase(UserGateway userGateway, PasswordHasherGateway passwordHasherGateway) {
        this.userGateway = userGateway;
        this.passwordHasherGateway = passwordHasherGateway;
    }

    public User execute(User user) {
        if (userGateway.findByEmail(user.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String protectedPassword = passwordHasherGateway.hash(user.password());
        user.password(protectedPassword);
        User userToSave = new User(null,user.name(), user.email(), protectedPassword, Collections.singleton("USER"),true );
        return userGateway.save(userToSave);
    }
}
