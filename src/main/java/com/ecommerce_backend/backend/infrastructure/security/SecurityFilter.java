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

            if (!login.isEmpty()) {
                // Lógica para diferenciar Cliente de Utilizador Administrativo
                if (roles.contains("CUSTOMER")) {
                    customerGateway.findByEmail(login).ifPresent(customer -> {
                        setAuthentication(customer.email(), customer.password(), List.of("ROLE_CUSTOMER"));
                    });
                } else {
                    userGateway.findByEmail(login).ifPresent(user -> {
                        setAuthentication(user.email(), user.password(), user.roles().stream().map(r -> "ROLE_" + r).toList());
                    });
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String email, String password, List<String> roles) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(password)
                .authorities(roles.toArray(new String[0]))
                .build();

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // O método que estava em falta ou com erro:
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}