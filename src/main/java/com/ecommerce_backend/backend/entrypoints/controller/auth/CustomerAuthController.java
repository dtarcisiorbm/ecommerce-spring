package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.useCases.create.AuthenticateCustomerUseCase;
import com.ecommerce_backend.backend.core.useCases.create.CreateCustomerUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.CustomerRequest;
import com.ecommerce_backend.backend.entrypoints.dto.LoginRequest;
import com.ecommerce_backend.backend.entrypoints.dto.LoginResponse;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.core.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/customer/auth")
public class CustomerAuthController {

    private final AuthenticateCustomerUseCase authenticateCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase; // Adicionado
    private final TokenServiceGateway tokenService;

    public CustomerAuthController(
            AuthenticateCustomerUseCase authenticateCustomerUseCase,
            CreateCustomerUseCase createCustomerUseCase,
            TokenServiceGateway tokenService) {
        this.authenticateCustomerUseCase = authenticateCustomerUseCase;
        this.createCustomerUseCase = createCustomerUseCase;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid CustomerRequest request) {
        Customer newCustomer = new Customer(
                null,
                request.fullName(),
                request.email(),
                request.taxId(),
                request.password()
        );

        createCustomerUseCase.execute(newCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        Customer customer = authenticateCustomerUseCase.execute(request.email(), request.password());

        User userForToken = new User(
                UUID.randomUUID(),
                customer.fullName(),
                customer.email(),
                "",
                Collections.singleton("CUSTOMER"),
                true
        );

        String token = tokenService.generateToken(userForToken);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}