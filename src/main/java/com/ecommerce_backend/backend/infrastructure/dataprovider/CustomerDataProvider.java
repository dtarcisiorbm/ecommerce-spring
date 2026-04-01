package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.entrypoints.mapper.CustomerMapper;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component; // IMPORTANTE
import java.util.Optional;
import java.util.UUID;

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
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }
}
