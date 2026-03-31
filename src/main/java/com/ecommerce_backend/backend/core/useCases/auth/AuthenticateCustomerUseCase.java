package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;

import java.util.Collections;
import java.util.UUID;

public class AuthenticateCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PasswordHasherGateway hasher;

    public AuthenticateCustomerUseCase(CustomerGateway customerGateway, PasswordHasherGateway hasher) {
        this.customerGateway = customerGateway;
        this.hasher = hasher;
    }

    public Customer execute(String email, String password) {
        Customer customer = customerGateway.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha do cliente inválidos."));

        if (!hasher.matches(password, customer.password())) {
            throw new IllegalArgumentException("E-mail ou senha do cliente inválidos.");
        }

        return customer;
    }
}