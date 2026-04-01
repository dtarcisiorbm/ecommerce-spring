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
        Optional<Customer> customer = customerGateway.findByIdAndActive(id, true);
        if (customer.isEmpty()) {
            throw new IllegalArgumentException("Active customer not found with id: " + id);
        }
        
        // Realiza soft delete mantendo o histórico
        customerGateway.softDeleteById(id);
    }
}
