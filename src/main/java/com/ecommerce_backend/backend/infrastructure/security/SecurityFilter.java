package com.ecommerce_backend.backend.infrastructure.security;

import com.ecommerce_backend.backend.core.gateway.TokenServiceGateway;
import com.ecommerce_backend.backend.core.gateway.UserGateway;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenServiceGateway tokenService;
    private final UserGateway userGateway;

    public SecurityFilter(TokenServiceGateway tokenService, UserGateway userGateway) {
        this.tokenService = tokenService;
        this.userGateway = userGateway;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            var login = tokenService.validateToken(token);
            if (!login.isEmpty()) {
                userGateway.findByEmail(login).ifPresent(user -> {
                    // Adaptamos o nosso usuário de domínio para o formato que o Spring Security entende
                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(user.email())
                            .password(user.password())
                            .roles(user.roles().toArray(new String[0]))
                            .build();

                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}