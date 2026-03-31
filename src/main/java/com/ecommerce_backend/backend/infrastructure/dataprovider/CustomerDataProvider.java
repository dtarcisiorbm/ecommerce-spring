package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.CustomerMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.CustomerRepository;
import org.springframework.stereotype.Component; // IMPORTANTE
import java.util.Optional;

@Component
public class CustomerDataProvider implements CustomerGateway {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public CustomerDataProvider(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Customer save(Customer customer) {
        var entity = mapper.toEntity(customer);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }
}