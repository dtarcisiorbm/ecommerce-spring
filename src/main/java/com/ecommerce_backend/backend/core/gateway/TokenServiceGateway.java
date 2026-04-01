package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.domain.User;
import java.util.List;

public interface TokenServiceGateway {
    String generateToken(User user);
    String generateToken(Customer customer);
    String validateToken(String token);
    List<String> getRoles(String token); // Novo método para ler as roles
}