package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.User;

import java.util.Optional;

public interface UserGateway {
    User save(User user);
    Optional<User> findByEmail(String email);
}
