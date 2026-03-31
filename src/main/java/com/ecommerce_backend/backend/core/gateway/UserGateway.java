package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserGateway {
    User save(User user);

    Page<User> findAll(Pageable pageable);

    Optional<User> findByEmail(String email);
}
