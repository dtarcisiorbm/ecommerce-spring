package com.ecommerce_backend.backend.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class JwtTokenAdapter implements TokenServiceGateway {

    @Value("${api.security.token.secret:my-secret-key}")
    private String secret;

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // Convertemos o Set para uma ArrayList padrão para evitar problemas de compatibilidade
            List<String> rolesList = new ArrayList<>(user.roles());

            return JWT.create()
                    .withIssuer("ecommerce-backend")
                    .withSubject(user.email())
                    .withClaim("roles", rolesList) // Adiciona as permissões no payload
                    .withExpiresAt(genExpirationDate()) // Agora compatível com Instant na v4.4.0
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token", exception);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ecommerce-backend")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception exception) {
            return "";
        }
    }

    @Override
    public List<String> getRoles(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ecommerce-backend")
                    .build()
                    .verify(token)
                    .getClaim("roles")
                    .asList(String.class);
        } catch (Exception exception) {
            return List.of();
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}