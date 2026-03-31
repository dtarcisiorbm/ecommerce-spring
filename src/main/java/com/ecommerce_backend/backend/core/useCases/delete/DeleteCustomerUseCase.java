package com.ecommerce_backend.backend.core.useCases.delete;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeleteCustomerUseCase {
    private final CustomerGateway customerGateway;

    public DeleteCustomerUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public void execute(UUID id) {
        Optional<Customer> customer = customerGateway.findById(id);
        customer.ifPresent(c -> customerGateway.deleteById(id));
    }
}
