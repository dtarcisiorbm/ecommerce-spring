package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.useCases.auth.AuthenticateUserUseCase;
import com.ecommerce_backend.backend.core.useCases.create.AuthenticateCustomerUseCase;
import com.ecommerce_backend.backend.core.useCases.create.CreateCustomerUseCase;
import com.ecommerce_backend.backend.core.useCases.create.CreateUserUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.*;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.infrastructure.ratelimit.RateLimit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final AuthenticateCustomerUseCase authenticateCustomerUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final TokenServiceGateway tokenService;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            CreateUserUseCase createUserUseCase,
            AuthenticateCustomerUseCase authenticateCustomerUseCase,
            CreateCustomerUseCase createCustomerUseCase,
            TokenServiceGateway tokenService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.createUserUseCase = createUserUseCase;
        this.authenticateCustomerUseCase = authenticateCustomerUseCase;
        this.createCustomerUseCase = createCustomerUseCase;
        this.tokenService = tokenService;
    }

    /**
     * Registra um novo usuário administrativo
     */
    @PostMapping("/register")
    @RateLimit(requests = 5, windowMinutes = 5, message = "Too many registration attempts. Please try again later.")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterRequest request) {
        User newUser = new User(
                null,
                request.name(),
                request.email(),
                request.password(),
                request.roles() != null ? request.roles() : Collections.singleton("USER"),
                true
        );

        createUserUseCase.execute(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Registra um novo usuário administrador com papel ADMIN
     */
    @PostMapping("/register/admin")
    @RateLimit(requests = 3, windowMinutes = 10, message = "Too many admin registration attempts. Please try again later.")
    public ResponseEntity<Void> registerAdmin(@RequestBody @Valid RegisterRequest request) {
        User adminUser = new User(
                null,
                request.name(),
                request.email(),
                request.password(),
                Collections.singleton("ADMIN"),
                true
        );

        createUserUseCase.execute(adminUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Registra um novo cliente
     */
    @PostMapping("/customer/register")
    @RateLimit(requests = 5, windowMinutes = 5, message = "Too many registration attempts. Please try again later.")
    public ResponseEntity<Void> registerCustomer(@RequestBody @Valid CustomerRequest request) {
        Customer newCustomer = new Customer(
                null,
                request.fullName(),
                request.email(),
                request.taxId(),
                request.password(),
                true,
                null,
                null
        );

        createCustomerUseCase.execute(newCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Autentica usuário administrativo
     */
    @PostMapping("/login")
    @RateLimit(requests = 10, windowMinutes = 1, message = "Too many login attempts. Please try again later.")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest request) {
        User user = authenticateUserUseCase.execute(request.email(), request.password());
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Autentica cliente
     */
    @PostMapping("/customer/login")
    @RateLimit(requests = 10, windowMinutes = 1, message = "Too many login attempts. Please try again later.")
    public ResponseEntity<LoginResponse> loginCustomer(@RequestBody @Valid LoginRequest request) {
        Customer customer = authenticateCustomerUseCase.execute(request.email(), request.password());
        
        // Cria User para geração de token mantendo compatibilidade
        User userForToken = new User(
                customer.id(),
                customer.fullName(),
                customer.email(),
                "", // senha não necessária para token
                Collections.singleton("CUSTOMER"),
                true
        );

        String token = tokenService.generateToken(userForToken);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    /**
     * Valida token (endpoint útil para frontend)
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") @NotBlank String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header format");
        }

        String token = authHeader.substring(7);
        String subject = tokenService.validateToken(token);
        
        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        return ResponseEntity.ok(new TokenValidationResponse(true, subject));
    }

    /**
     * Refresh token (endpoint para renovação de token)
     */
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestHeader("Authorization") @NotBlank String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header format");
        }

        String token = authHeader.substring(7);
        String subject = tokenService.validateToken(token);
        
        if (subject.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        // Aqui você poderia buscar o usuário novamente para obter dados atualizados
        // Por simplicidade, estamos apenas validando que o token é válido
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
