package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Customer;

import java.util.Optional;

public class CustomerGateway {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByEmail(String email);
}
