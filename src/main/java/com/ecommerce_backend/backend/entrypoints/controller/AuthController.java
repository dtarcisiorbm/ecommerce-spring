package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.core.useCases.AuthenticateUserUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.LoginRequest;
import com.ecommerce_backend.backend.entrypoints.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final TokenServiceGateway tokenService; // Injeção do novo serviço

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase, TokenServiceGateway tokenService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        User user = authenticateUserUseCase.execute(request.email(), request.password());

        // Gera o token real para o utilizador autenticado
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}