package com.ecommerce_backend.backend.core.useCases.find;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FindCustomerByIdUseCase {
    private final CustomerGateway customerGateway;

    public FindCustomerByIdUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public Optional<Customer> execute(UUID id) {
        return customerGateway.findById(id);
    }
}
