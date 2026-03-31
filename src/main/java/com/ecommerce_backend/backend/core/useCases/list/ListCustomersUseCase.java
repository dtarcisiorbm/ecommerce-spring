package com.ecommerce_backend.backend.core.useCases.list;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListCustomersUseCase {
    private final CustomerGateway customerGateway;

    public ListCustomersUseCase(CustomerGateway customerGateway) {
        this.customerGateway = customerGateway;
    }

    public Page<Customer> execute(Pageable pageable) {
        return customerGateway.findAll(pageable);
    }
}
