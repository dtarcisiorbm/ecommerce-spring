package com.ecommerce_backend.backend.core.useCases.create;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.core.gateway.PasswordHasherGateway;

public class CreateCustomerUseCase {

    private final CustomerGateway customerGateway;
    private final PasswordHasherGateway passwordHasher; // Adicionado

    public CreateCustomerUseCase(CustomerGateway customerGateway, PasswordHasherGateway passwordHasher) {
        this.customerGateway = customerGateway;
        this.passwordHasher = passwordHasher;
    }

    public Customer execute(Customer customer) {
        if (customerGateway.findByEmail(customer.email()).isPresent()) {
            throw new IllegalArgumentException("Cliente com este e-mail já existe.");
        }

        // Encripta a senha antes de persistir
        String encryptedPassword = passwordHasher.hash(customer.password());

        Customer customerToSave = new Customer(
                null,
                customer.fullName(),
                customer.email(),
                customer.taxId(),
                encryptedPassword
        );

        return customerGateway.save(customerToSave);
    }
}