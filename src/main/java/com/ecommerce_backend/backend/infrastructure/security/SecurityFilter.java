package com.ecommerce_backend.backend.infrastructure.security;

import com.ecommerce_backend.backend.core.gateway.CustomerGateway;
import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenServiceGateway tokenService;
    private final UserGateway userGateway;
    private final CustomerGateway customerGateway;

    public SecurityFilter(TokenServiceGateway tokenService, UserGateway userGateway, CustomerGateway customerGateway) {
        this.tokenService = tokenService;
        this.userGateway = userGateway;
        this.customerGateway = customerGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Chamada correta do método auxiliar
        var token = this.recoverToken(request);

        if (token != null) {
            var login = tokenService.validateToken(token);
            var roles = tokenService.getRoles(token);

            System.out.println("Token found: " + token.substring(0, Math.min(20, token.length())) + "...");
            System.out.println("Login from token: '" + login + "'");
            System.out.println("Roles from token: " + roles);
            System.out.println("Login is empty: " + login.isEmpty());

            if (!login.isEmpty()) {
                // Lógica para diferenciar Cliente de Utilizador Administrativo
                if (roles.contains("CUSTOMER")) {
                    System.out.println("Processing as CUSTOMER");
                    customerGateway.findByEmail(login).ifPresentOrElse(customer -> {
                        System.out.println("Customer found: " + customer.email() + " with ID: " + customer.id());
                        setAuthentication(customer.email(), customer.password(), List.of("CUSTOMER"), customer.id());
                    }, () -> {
                        System.out.println("Customer NOT found for email: " + login);
                    });
                } else {
                    System.out.println("Processing as ADMIN");
                    userGateway.findByEmail(login).ifPresent(user -> {
                        System.out.println("Setting authentication for ADMIN with roles: " + user.roles());
                        setAuthentication(user.email(), user.password(), user.roles().stream().toList());
                    });
                }
            } else {
                System.out.println("Login is empty, skipping authentication");
            }
        } else {
            System.out.println("No token found in request");
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String email, String password, List<String> roles) {
        setAuthentication(email, password, roles, null);
    }

    private void setAuthentication(String email, String password, List<String> roles, UUID customerId) {
        // Adiciona prefixo ROLE_ às authorities para compatibilidade com Spring Security
        String[] authoritiesWithPrefix = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toArray(String[]::new);
        
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(password)
                .authorities(authoritiesWithPrefix)
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        
        // Adiciona o customerId aos detalhes da autenticação se disponível
        if (customerId != null) {
            authentication.setDetails(customerId);
        }
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // O método que estava em falta ou com erro:
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}