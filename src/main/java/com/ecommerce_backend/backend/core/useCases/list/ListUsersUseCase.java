package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ListUsersUseCase {
    private final UserGateway userGateway;

        public ListUsersUseCase( UserGateway userGateway) {
            this.userGateway = userGateway;

    }

    public Page<User> execute(Pageable pageable) {
        return userGateway.findAll(pageable);
    }
}