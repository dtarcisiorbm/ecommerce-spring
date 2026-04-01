package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomerGateway {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByIdAndActive(UUID id, boolean active);
    void softDeleteById(UUID id);
    Page<Customer> findAll(Pageable pageable);
    Page<Customer> findAllActive(Pageable pageable);
}
