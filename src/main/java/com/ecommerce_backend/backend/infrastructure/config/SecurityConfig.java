package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.infrastructure.ratelimit.RateLimitFilter;
import com.ecommerce_backend.backend.infrastructure.security.SecurityFilter;
import com.ecommerce_backend.backend.entrypoints.exceptions.SecurityExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(SecurityFilter securityFilter, RateLimitFilter rateLimitFilter) {
        this.securityFilter = securityFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos de autenticação
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/customer/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/customer/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/validate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").authenticated()
                        
                        // Endpoints de documentação (se houver)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // Endpoints básicos para usuários autenticados
                        .requestMatchers(HttpMethod.GET, "/products/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/categories/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/customers/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                        
                        // ADMIN tem acesso total a todas as outras rotas
                        .requestMatchers("/**").hasRole("ADMIN")
                        
                        // Demais endpoints requerem autenticação
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Authentication required: No valid access token provided",
                                    "details": "Please include a valid Bearer token in the Authorization header",
                                    "timestamp": "%s",
                                    "path": "%s"
                                }
                                """.formatted(
                                    java.time.LocalDateTime.now(),
                                    request.getRequestURI()
                                )
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("""
                                {
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Access denied: Insufficient permissions for this resource",
                                    "details": "Your token is valid but you don't have the required role/permission",
                                    "timestamp": "%s",
                                    "path": "%s"
                                }
                                """.formatted(
                                    java.time.LocalDateTime.now(),
                                    request.getRequestURI()
                                )
                            );
                        })
                )
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
