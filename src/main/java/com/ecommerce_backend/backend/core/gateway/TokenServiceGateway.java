package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.User;

public interface TokenServiceGateway {
    String generateToken(User user);
    String validateToken(String token);
}