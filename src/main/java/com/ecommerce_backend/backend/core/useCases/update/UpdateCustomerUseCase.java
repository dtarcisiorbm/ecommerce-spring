package com.ecommerce_backend.backend.core.useCases.update;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateCustomerUseCase {
    private final CustomerGateway customerGateway;

    public UpdateCustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public Customer execute(Customer customer) {
        // Verifica se o cliente existe e está ativo
        Optional<Customer> existingCustomer = customerGateway.findByIdAndActive(customer.id(), true);
        if (existingCustomer.isEmpty()) {
            throw new IllegalArgumentException("Active customer not found with id: " + customer.id());
        }

        // Verifica se o email já está em uso por outro cliente
        Optional<Customer> customerByEmail = customerGateway.findByEmail(customer.email());
        if (customerByEmail.isPresent() && !customerByEmail.get().id().equals(customer.id())) {
            throw new IllegalArgumentException("Email already in use by another customer");
        }

        // Mantém os campos de auditoria existentes
        Customer originalCustomer = existingCustomer.get();
        
        // Se senha não for fornecida, mantém a atual
        String passwordToUse = (customer.password() != null && !customer.password().trim().isEmpty()) 
                ? customer.password() 
                : originalCustomer.password();

        Customer updatedCustomer = new Customer(
                customer.id(),
                customer.fullName(),
                customer.email(),
                customer.taxId(),
                passwordToUse,
                originalCustomer.active(), // Mantém status ativo
                originalCustomer.createdAt(),
                null // Será definido pelo construtor
        );

        return customerGateway.save(updatedCustomer);
    }
}
