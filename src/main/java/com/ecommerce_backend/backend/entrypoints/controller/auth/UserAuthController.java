package com.ecommerce_backend.backend.entrypoints.controller.auth;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.core.useCases.auth.AuthenticateUserUseCase;
import com.ecommerce_backend.backend.core.useCases.create.CreateUserUseCase;
import com.ecommerce_backend.backend.entrypoints.dto.LoginRequest;
import com.ecommerce_backend.backend.entrypoints.dto.LoginResponse;
import com.ecommerce_backend.backend.entrypoints.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserAuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final TokenServiceGateway tokenService; // Injeção do novo serviço
    private final CreateUserUseCase createUserUseCase;
    public UserAuthController(AuthenticateUserUseCase authenticateUserUseCase, TokenServiceGateway tokenService, CreateUserUseCase createUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.tokenService = tokenService;
        this.createUserUseCase=createUserUseCase;
    }
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterRequest request) {
        // Agora o domínio recebe os dados validados e mapeados corretamente
        User newUser = new User(
                null,
                request.name(),
                request.email(),
                request.password(),
                null,
                true
        ); //

        createUserUseCase.execute(newUser); //
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        User user = authenticateUserUseCase.execute(request.email(), request.password());

        // Gera o token real para o utilizador autenticado
        String token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}