package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.useCases.create.CreateCustomerUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.CustomerRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody @Valid CustomerRequest request) {
        // Mapeamento manual do DTO para o Domínio
        var customerDomain = new Customer(
                null,
                request.fullName(),
                request.email(),
                request.taxId(),
                request.password()
        );

        Customer created = createCustomerUseCase.execute(customerDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}